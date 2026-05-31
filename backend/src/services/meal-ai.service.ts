import { GoogleGenAI, Type } from "@google/genai";
import { MealType, MealStatus } from "@prisma/client";
import { prisma } from "../lib/prisma.js";
import { loadEnv } from "../config/env.js";

const env = loadEnv();
const ai = new GoogleGenAI({ apiKey: env.GEMINI_API_KEY });

function getRecipeImageUrl(title: string): string {
  const t = title.toLowerCase();
  if (t.includes("bò") || t.includes("beef")) {
    return "https://images.unsplash.com/photo-1544025162-d76694265947?q=80&w=600&auto=format&fit=crop";
  }
  if (t.includes("hồi") || t.includes("salmon")) {
    return "https://images.unsplash.com/photo-1467003909585-2f8a72700288?q=80&w=600&auto=format&fit=crop";
  }
  if (t.includes("trứng") || t.includes("egg")) {
    return "https://images.unsplash.com/photo-1525351484163-7529414344d8?q=80&w=600&auto=format&fit=crop";
  }
  if (t.includes("gà") || t.includes("chicken") || t.includes("fowl")) {
    return "https://images.unsplash.com/photo-1604908176997-125f25cc6f3d?q=80&w=600&auto=format&fit=crop";
  }
  if (
    t.includes("heo") ||
    t.includes("lợn") ||
    t.includes("pork") ||
    t.includes("sườn")
  ) {
    return "https://images.unsplash.com/photo-1544025162-d76694265947?q=80&w=600&auto=format&fit=crop";
  }
  if (
    t.includes("rau") ||
    t.includes("salad") ||
    t.includes("chay") ||
    t.includes("củ")
  ) {
    return "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?q=80&w=600&auto=format&fit=crop";
  }
  if (
    t.includes("tôm") ||
    t.includes("shrimp") ||
    t.includes("hải sản") ||
    t.includes("seafood") ||
    t.includes("cá")
  ) {
    return "https://images.unsplash.com/photo-1565557623262-b51c2513a641?q=80&w=600&auto=format&fit=crop";
  }
  return "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?q=80&w=600&auto=format&fit=crop";
}

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
    .map((i) => `- ${i.name} (${i.quantity} ${i.unit})`)
    .join("\n");

  const availableText = availableItems
    .map((i) => `- ${i.name} (${i.quantity} ${i.unit})`)
    .join("\n");

  const prompt = `Bạn là một đầu bếp thông minh siêu hạng. Nhiệm vụ của bạn là cứu rỗi các nguyên liệu sắp hết hạn.
Hãy tạo 1 công thức nấu ăn xuất sắc tận dụng tối đa các nguyên liệu sắp hết hạn sau đây:
[NGUYÊN LIỆU SẮP HẾT HẠN - BẮT BUỘC DÙNG NHIỀU NHẤT CÓ THỂ]
${expiringText || "Không có nguyên liệu nào sắp hết hạn."}

[CÁC NGUYÊN LIỆU KHÁC ĐANG CÓ TRONG TỦ]
${availableText || "Không có."}

Bạn có thể thêm các gia vị cơ bản hoặc một vài nguyên liệu ngoài (nếu thực sự cần thiết).

QUAN TRỌNG: Đối với những nguyên liệu lấy từ tủ lạnh (được liệt kê ở trên), bạn BẮT BUỘC phải viết Tên nguyên liệu ('name') và Đơn vị tính ('unit') trùng khớp hoàn toàn (không viết tắt hay đổi tên, đổi đơn vị) với danh sách được cung cấp ở trên.
Hãy trả về JSON theo đúng định dạng.`;

  const response = await ai.models.generateContent({
    model: env.GEMINI_MODEL,
    contents: prompt,
    config: {
      responseMimeType: "application/json",
      responseSchema: {
        type: Type.OBJECT,
        properties: {
          title: {
            type: Type.STRING,
            description: "Tên món ăn ngắn gọn (Tối đa 30 ký tự)",
          },
          description: {
            type: Type.STRING,
            description: "Mô tả ngắn gọn, súc tích về món ăn (Tối đa 80 ký tự)",
          },
          ingredients: {
            type: Type.ARRAY,
            items: {
              type: Type.OBJECT,
              properties: {
                name: { type: Type.STRING },
                quantity: { type: Type.NUMBER },
                unit: { type: Type.STRING },
              },
              required: ["name"],
            },
            description: "Nguyên liệu cần thiết",
          },
          instructions: {
            type: Type.ARRAY,
            items: { type: Type.STRING },
            description: "Các bước nấu (từng bước một)",
          },
          prepTime: { type: Type.INTEGER, description: "Phút chuẩn bị" },
          calories: { type: Type.INTEGER, description: "Calo ước tính" },
        },
        required: ["title", "ingredients", "instructions"],
      },
    },
  });

  if (!response.text) {
    throw new Error("AI failed to generate recipe");
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
      imageUrl: getRecipeImageUrl(parsed.title),
      prepTime: parsed.prepTime || null,
      calories: parsed.calories || null,
    },
  });

  return meal;
}

export async function suggestMealFromItem(
  userId: string,
  targetItemId: string,
) {
  const env = loadEnv();
  const ai = new GoogleGenAI({ apiKey: env.GEMINI_API_KEY });

  // 1. Lấy nguyên liệu chủ đạo
  const targetItem = await prisma.inventoryItem.findFirst({
    where: { id: targetItemId, userId },
  });
  if (!targetItem) throw new Error("Target item not found");

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
    .map((i) => `- ${i.name} (${i.quantity} ${i.unit})`)
    .join("\n");

  const availableText = availableItems
    .map((i) => `- ${i.name} (${i.quantity} ${i.unit})`)
    .join("\n");

  const prompt = `Bạn là một đầu bếp thông minh siêu hạng.
Hãy tạo 1 công thức nấu ăn xuất sắc với nguyên liệu CHỦ ĐẠO là:
[NGUYÊN LIỆU CHÍNH - BẮT BUỘC PHẢI LÀ TÂM ĐIỂM CỦA MÓN ĂN]
- ${targetItem.name} (${targetItem.quantity} ${targetItem.unit})

Đồng thời, bạn BẮT BUỘC phải cố gắng kết hợp với các nguyên liệu sắp hết hạn sau đây:
[NGUYÊN LIỆU SẮP HẾT HẠN CẦN ƯU TIÊN]
${expiringText || "Không có nguyên liệu nào sắp hết hạn."}

[CÁC NGUYÊN LIỆU KHÁC ĐANG CÓ TRONG TỦ]
${availableText || "Không có."}

Bạn có thể thêm các gia vị cơ bản hoặc một vài nguyên liệu ngoài (nếu thực sự cần thiết).

QUAN TRỌNG: Đối với những nguyên liệu lấy từ tủ lạnh (được liệt kê ở trên), bạn BẮT BUỘC phải viết Tên nguyên liệu ('name') và Đơn vị tính ('unit') trùng khớp hoàn toàn (không viết tắt hay đổi tên, đổi đơn vị) với danh sách được cung cấp ở trên.
Hãy trả về JSON theo đúng định dạng.`;

  let parsed;
  try {
    const response = await ai.models.generateContent({
      model: env.GEMINI_MODEL,
      contents: prompt,
      config: {
        responseMimeType: "application/json",
        responseSchema: {
          type: Type.OBJECT,
          properties: {
            title: {
              type: Type.STRING,
              description: "Tên món ăn ngắn gọn (Tối đa 30 ký tự)",
            },
            description: {
              type: Type.STRING,
              description:
                "Mô tả ngắn gọn, súc tích về món ăn (Tối đa 80 ký tự)",
            },
            ingredients: {
              type: Type.ARRAY,
              items: {
                type: Type.OBJECT,
                properties: {
                  name: { type: Type.STRING },
                  quantity: { type: Type.NUMBER },
                  unit: { type: Type.STRING },
                },
                required: ["name"],
              },
              description: "Nguyên liệu cần thiết",
            },
            instructions: {
              type: Type.ARRAY,
              items: { type: Type.STRING },
              description: "Các bước nấu (từng bước một)",
            },
            prepTime: { type: Type.INTEGER, description: "Phút chuẩn bị" },
            calories: { type: Type.INTEGER, description: "Calo ước tính" },
          },
          required: ["title", "ingredients", "instructions"],
        },
      },
    });

    if (!response.text) {
      throw new Error("AI failed to generate recipe");
    }
    parsed = JSON.parse(response.text);
  } catch (error) {
    console.warn("Gemini API Error, falling back to mock generation:", error);
    const itemNameLower = targetItem.name.toLowerCase();
    if (itemNameLower.includes("bò")) {
      parsed = {
        title: "Ba Chỉ Bò Xào Hành Cần",
        description:
          "Món ăn nhanh, thơm ngon, đậm đà vị tẩm ướp cùng thịt bò ba chỉ.",
        ingredients: [
          {
            name: targetItem.name,
            quantity: targetItem.quantity,
            unit: targetItem.unit,
          },
          { name: "Hành tây", quantity: 1, unit: "củ" },
          { name: "Tỏi", quantity: 3, unit: "tép" },
          { name: "Dầu hào", quantity: 1, unit: "muỗng" },
        ],
        instructions: [
          "Thịt bò thái mỏng, ướp chút gia vị, dầu ăn và tỏi băm.",
          "Hành tây bổ múi cau, cần tây cắt khúc.",
          "Phi thơm tỏi, cho thịt bò vào xào nhanh tay lửa lớn rồi trút ra đĩa riêng.",
          "Tiếp tục xào hành tây chín tái, đổ thịt bò vào đảo cùng rồi tắt bếp.",
        ],
        prepTime: 15,
        calories: 450,
      };
    } else if (itemNameLower.includes("hồi")) {
      parsed = {
        title: "Cá Hồi Áp Chảo Sốt Chanh Leo",
        description:
          "Hương vị chua ngọt thanh mát của chanh leo kết hợp với cá hồi béo ngậy cực kỳ chuẩn vị.",
        ingredients: [
          {
            name: targetItem.name,
            quantity: targetItem.quantity,
            unit: targetItem.unit,
          },
          { name: "Chanh leo", quantity: 2, unit: "quả" },
          { name: "Bơ nhạt", quantity: 10, unit: "g" },
          { name: "Đường", quantity: 1, unit: "muỗng" },
        ],
        instructions: [
          "Cá hồi áp chảo vàng đều hai mặt với chút bơ nhạt.",
          "Chanh leo lọc lấy nước cốt, đun sôi cùng đường và chút muối cho hơi sánh.",
          "Rưới nước sốt chanh leo lên cá hồi chín và thưởng thức kèm salad.",
        ],
        prepTime: 20,
        calories: 520,
      };
    } else if (itemNameLower.includes("trứng")) {
      parsed = {
        title: "Trứng Cuộn Rau Củ Chiên",
        description:
          "Món trứng cuộn màu sắc bắt mắt, giàu dinh dưỡng và dễ làm.",
        ingredients: [
          {
            name: targetItem.name,
            quantity: targetItem.quantity,
            unit: targetItem.unit,
          },
          { name: "Hành hoa", quantity: 2, unit: "nhánh" },
          { name: "Cà rốt", quantity: 0.25, unit: "củ" },
        ],
        instructions: [
          "Đập trứng ra bát, đánh đều với chút hạt nêm, hành lá và cà rốt băm nhỏ.",
          "Đổ một lớp mỏng trứng vào chảo chống dính nóng, cuộn tròn lại khi trứng hơi đông.",
          "Tiếp tục đổ lớp tiếp theo và cuộn tiếp cho tới khi hết trứng, thái miếng vừa ăn.",
        ],
        prepTime: 10,
        calories: 220,
      };
    } else {
      parsed = {
        title: `Món Xào Thập Cẩm Với ${targetItem.name}`,
        description: `Tận dụng tối đa ${targetItem.name} kết hợp cùng rau củ dọn tủ lạnh tiện lợi.`,
        ingredients: [
          {
            name: targetItem.name,
            quantity: targetItem.quantity,
            unit: targetItem.unit,
          },
          { name: "Hành tỏi", quantity: 2, unit: "củ" },
          { name: "Gia vị thông dụng", quantity: 1, unit: "phần" },
        ],
        instructions: [
          `Sơ chế sạch ${targetItem.name} và thái miếng vừa ăn.`,
          "Phi hành tỏi băm thơm lừng trên chảo nóng.",
          `Cho ${targetItem.name} vào xào chín tới cùng gia vị, bày ra đĩa ăn nóng.`,
        ],
        prepTime: 12,
        calories: 300,
      };
    }
  }

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
      imageUrl: getRecipeImageUrl(parsed.title),
      prepTime: parsed.prepTime || null,
      calories: parsed.calories || null,
    },
  });

  return meal;
}

export async function voiceAssistant(userId: string, text: string) {
  try{
    // Configure the client
  const ai = new GoogleGenAI({});

  // Define the function declaration for the model
  const checkIngredientFunctionDeclaration = {
    name: "check_ingredient",
    description:
      "Kiểm tra xem một loại thực phẩm còn trong tủ lạnh không và số lượng bao nhiêu.",
    parameters: {
      type: Type.OBJECT,
      properties: {
        tenMon: {
          type: Type.STRING,
          description: "Tên loại thực phẩm, ví dụ: trứng, sữa, cà chua",
        },
      },
      required: ["tenMon"],
    },
 
  };
  const addIngredientFunctionDeclaration = {
    name: "add_ingredient",
    description: "Thêm thực phẩm vào tủ lạnh.",
    parameters: {
      type: Type.OBJECT,
      properties: {
        name: {
          type: Type.STRING,
          description: "Tên thực phẩm",
        },
        quantity: {
          type: Type.NUMBER,
          description: "Số lượng",
        },
        unit: {
          type: Type.STRING,
          description: "Đơn vị tính",
        },
      },
      required: ["name", "quantity", "unit"],
    },
  }

  // Send request with function declarations
  const response = await ai.models.generateContent({
    model: "gemini-2.5-flash",
    contents: [{ role: "user", parts: [{ text: text }] }],
    config: {
      tools: [
        {
          functionDeclarations: [checkIngredientFunctionDeclaration, addIngredientFunctionDeclaration],
        },
      ],
    },
  });

  // Check for function calls in the response
  if (response.functionCalls && response.functionCalls.length > 0) {
    const functionCall = response.functionCalls[0]; // Assuming one function call
    if (functionCall.name === "check_ingredient") {
      const args = functionCall.args as any;
      const ingredient = await prisma.inventoryItem.findFirst({
        where: {
          userId,
          name: {
            contains: args.tenMon as string,
            mode: 'insensitive'
          },
          quantity: { gt: 0 },
          isWasted: false,
        },
      });
      if (ingredient) {
        return {
          success: true,
          ingredient,
          message: `Trong tủ lạnh của bạn vẫn còn ${ingredient.name} nhé! Số lượng hiện tại là ${ingredient.quantity} ${ingredient.unit || ""}.`,
        };
      } else {
        console.log(args.tenMon)
        return {
          success: false,
          ingredient: null,
          message: `Không tìm thấy ${args.tenMon} trong tủ lạnh của bạn.`,
        };
      }
    } else if (functionCall.name === "add_ingredient") {
      const args = functionCall.args as any;
      const name = args.name as string;
      const quantity = parseFloat(args.quantity || "1");
      const unit = args.unit as string;

      // Guess compartment
      const nameLower = name.toLowerCase();
      let compartment: "FREEZER" | "FRIDGE" | "DOOR" | "CRISPER" = "FRIDGE";
      if (nameLower.includes("thịt") || nameLower.includes("cá") || nameLower.includes("bò") || nameLower.includes("heo") || nameLower.includes("gà") || nameLower.includes("tôm") || nameLower.includes("hải sản")) {
        compartment = "FREEZER";
      } else if (nameLower.includes("rau") || nameLower.includes("củ") || nameLower.includes("quả") || nameLower.includes("xà lách") || nameLower.includes("cà chua") || nameLower.includes("nấm")) {
        compartment = "CRISPER";
      } else if (nameLower.includes("sữa") || nameLower.includes("trứng") || nameLower.includes("nước") || nameLower.includes("lon") || nameLower.includes("bia") || nameLower.includes("coca")) {
        compartment = "DOOR";
      }

      // Guess category
      let category = "Khác";
      if (compartment === "FREEZER") {
        category = "Thịt cá";
      } else if (compartment === "CRISPER") {
        category = "Rau củ";
      } else if (nameLower.includes("sữa") || nameLower.includes("trứng")) {
        category = "Sữa trứng";
      }

      const expiryDate = new Date();
      expiryDate.setDate(expiryDate.getDate() + 7); // Default expiry 7 days

      const newItem = await prisma.inventoryItem.create({
        data: {
          userId,
          name,
          quantity,
          unit,
          category,
          compartment,
          expiryDate,
          entryMethod: "VOICE",
          isWasted: false
        }
      });

      const compartmentName = compartment === "FREEZER" ? "Ngăn đá" : 
                              compartment === "CRISPER" ? "Khay rau củ" : 
                              compartment === "DOOR" ? "Cánh tủ" : "Ngăn mát";

      return {
        success: true,
        ingredient: newItem,
        message: `Đã thêm ${quantity} ${unit} ${name} vào ${compartmentName} thành công nhé!`,
      };
    }
  } 
  else {
    console.log(response.text)
    return {
      success: false,
      ingredient: null,
      message: `Tôi không nhận diện được món ăn hoặc tủ lạnh không có món đó.`,
    };
  }
  } catch(error){
    console.log(error)
  }
  
}
