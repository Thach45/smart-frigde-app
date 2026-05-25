import { GoogleGenAI, Type } from '@google/genai';
import { MealType, MealStatus } from '@prisma/client';
import { prisma } from '../lib/prisma.js';
import { loadEnv } from '../config/env.js';

const env = loadEnv();
const ai = new GoogleGenAI({ apiKey: env.GEMINI_API_KEY });

export async function suggestMeal(userId: string) {
  // Lấy danh sách đồ ăn sắp hết hạn (trong vòng 3 ngày tới)
  const threeDaysFromNow = new Date();
  threeDaysFromNow.setDate(threeDaysFromNow.getDate() + 3);
  
  const expiringItems = await prisma.inventoryItem.findMany({
    where: {
      userId,
      quantity: { gt: 0 },
      isWasted: false,
      expiryDate: { lte: threeDaysFromNow },
    },
  });

  // Lấy danh sách đồ ăn khác còn trong tủ (để phong phú nguyên liệu)
  const availableItems = await prisma.inventoryItem.findMany({
    where: {
      userId,
      quantity: { gt: 0 },
      isWasted: false,
      expiryDate: { gt: threeDaysFromNow },
    },
  });

  const expiringText = expiringItems
    .map(i => `- ${i.name} (${i.quantity} ${i.unit})`)
    .join('\n');

  const availableText = availableItems
    .map(i => `- ${i.name} (${i.quantity} ${i.unit})`)
    .join('\n');

  const prompt = `Bạn là một đầu bếp thông minh siêu hạng. Nhiệm vụ của bạn là cứu rỗi các nguyên liệu sắp hết hạn.
Hãy tạo 1 công thức nấu ăn xuất sắc tận dụng tối đa các nguyên liệu sắp hết hạn sau đây:
[NGUYÊN LIỆU SẮP HẾT HẠN - BẮT BUỘC DÙNG NHIỀU NHẤT CÓ THỂ]
${expiringText || 'Không có nguyên liệu nào sắp hết hạn.'}

[CÁC NGUYÊN LIỆU KHÁC ĐANG CÓ TRONG TỦ]
${availableText || 'Không có.'}

Bạn có thể thêm các gia vị cơ bản hoặc một vài nguyên liệu ngoài (nếu thực sự cần thiết).
Hãy trả về JSON theo đúng định dạng.`;

  const response = await ai.models.generateContent({
    model: env.GEMINI_MODEL,
    contents: prompt,
    config: {
      responseMimeType: 'application/json',
      responseSchema: {
        type: Type.OBJECT,
        properties: {
          title: { type: Type.STRING, description: "Tên món ăn" },
          description: { type: Type.STRING, description: "Mô tả ngắn" },
          ingredients: {
            type: Type.ARRAY,
            items: {
              type: Type.OBJECT,
              properties: {
                name: { type: Type.STRING },
                quantity: { type: Type.NUMBER },
                unit: { type: Type.STRING },
              },
              required: ["name"]
            },
            description: "Nguyên liệu cần thiết"
          },
          instructions: {
            type: Type.ARRAY,
            items: { type: Type.STRING },
            description: "Các bước nấu (từng bước một)"
          },
          prepTime: { type: Type.INTEGER, description: "Phút chuẩn bị" },
          calories: { type: Type.INTEGER, description: "Calo ước tính" },
        },
        required: ["title", "ingredients", "instructions"]
      }
    }
  });

  if (!response.text) {
    throw new Error('AI failed to generate recipe');
  }

  const parsed = JSON.parse(response.text);

  // Lưu món ăn vào Database với trạng thái SUGGESTED
  const meal = await prisma.meal.create({
    data: {
      userId,
      date: new Date(),
      mealType: MealType.DINNER, // Có thể cải tiến AI tự chọn MealType dựa theo món
      status: MealStatus.SUGGESTED,
      title: parsed.title,
      description: parsed.description,
      ingredients: parsed.ingredients.map((ing: any) => ({
        name: ing.name,
        quantity: ing.quantity || null,
        unit: ing.unit || null,
      })),
      instructions: parsed.instructions,
      prepTime: parsed.prepTime || null,
      calories: parsed.calories || null,
    }
  });

  return meal;
}

export async function suggestMealFromItem(userId: string, targetItemId: string) {
  const env = loadEnv();
  const ai = new GoogleGenAI({ apiKey: env.GEMINI_API_KEY });
  
  // 1. Lấy nguyên liệu chủ đạo
  const targetItem = await prisma.inventoryItem.findFirst({
    where: { id: targetItemId, userId }
  });
  if (!targetItem) throw new Error('Target item not found');

  // 2. Lấy các nguyên liệu sắp hết hạn (loại trừ targetItem nếu nó nằm trong danh sách)
  const threeDaysFromNow = new Date();
  threeDaysFromNow.setDate(threeDaysFromNow.getDate() + 3);
  
  const expiringItems = await prisma.inventoryItem.findMany({
    where: {
      userId,
      id: { not: targetItemId },
      quantity: { gt: 0 },
      isWasted: false,
      expiryDate: { lte: threeDaysFromNow },
    },
  });

  // 3. Lấy các nguyên liệu khác trong tủ
  const availableItems = await prisma.inventoryItem.findMany({
    where: {
      userId,
      id: { not: targetItemId },
      quantity: { gt: 0 },
      isWasted: false,
      expiryDate: { gt: threeDaysFromNow },
    },
  });

  const expiringText = expiringItems
    .map(i => `- ${i.name} (${i.quantity} ${i.unit})`)
    .join('\n');

  const availableText = availableItems
    .map(i => `- ${i.name} (${i.quantity} ${i.unit})`)
    .join('\n');

  const prompt = `Bạn là một đầu bếp thông minh siêu hạng.
Hãy tạo 1 công thức nấu ăn xuất sắc với nguyên liệu CHỦ ĐẠO là:
[NGUYÊN LIỆU CHÍNH - BẮT BUỘC PHẢI LÀ TÂM ĐIỂM CỦA MÓN ĂN]
- ${targetItem.name} (${targetItem.quantity} ${targetItem.unit})

Đồng thời, bạn BẮT BUỘC phải cố gắng kết hợp với các nguyên liệu sắp hết hạn sau đây:
[NGUYÊN LIỆU SẮP HẾT HẠN CẦN ƯU TIÊN]
${expiringText || 'Không có nguyên liệu nào sắp hết hạn.'}

[CÁC NGUYÊN LIỆU KHÁC ĐANG CÓ TRONG TỦ]
${availableText || 'Không có.'}

Bạn có thể thêm các gia vị cơ bản hoặc một vài nguyên liệu ngoài (nếu thực sự cần thiết).
Hãy trả về JSON theo đúng định dạng.`;

  const response = await ai.models.generateContent({
    model: env.GEMINI_MODEL,
    contents: prompt,
    config: {
      responseMimeType: 'application/json',
      responseSchema: {
        type: Type.OBJECT,
        properties: {
          title: { type: Type.STRING, description: "Tên món ăn" },
          description: { type: Type.STRING, description: "Mô tả ngắn" },
          ingredients: {
            type: Type.ARRAY,
            items: {
              type: Type.OBJECT,
              properties: {
                name: { type: Type.STRING },
                quantity: { type: Type.NUMBER },
                unit: { type: Type.STRING },
              },
              required: ["name"]
            },
            description: "Nguyên liệu cần thiết"
          },
          instructions: {
            type: Type.ARRAY,
            items: { type: Type.STRING },
            description: "Các bước nấu (từng bước một)"
          },
          prepTime: { type: Type.INTEGER, description: "Phút chuẩn bị" },
          calories: { type: Type.INTEGER, description: "Calo ước tính" },
        },
        required: ["title", "ingredients", "instructions"]
      }
    }
  });

  if (!response.text) {
    throw new Error('AI failed to generate recipe');
  }

  const parsed = JSON.parse(response.text);

  const meal = await prisma.meal.create({
    data: {
      userId,
      date: new Date(),
      mealType: MealType.DINNER,
      status: MealStatus.SUGGESTED,
      title: parsed.title,
      description: parsed.description,
      ingredients: parsed.ingredients.map((ing: any) => ({
        name: ing.name,
        quantity: ing.quantity || null,
        unit: ing.unit || null,
      })),
      instructions: parsed.instructions,
      prepTime: parsed.prepTime || null,
      calories: parsed.calories || null,
    }
  });

  return meal;
}
