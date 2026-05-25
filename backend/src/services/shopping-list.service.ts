import { MealStatus } from '@prisma/client';
import { prisma } from '../lib/prisma.js';

export async function acceptMealAndGenerateShoppingList(userId: string, mealId: string) {
  const meal = await prisma.meal.findFirst({
    where: { id: mealId, userId },
  });

  if (!meal) throw new Error('Meal not found');
  if (meal.status === MealStatus.ACCEPTED) throw new Error('Meal already accepted');

  const inventory = await prisma.inventoryItem.findMany({
    where: { userId, quantity: { gt: 0 }, isWasted: false },
  });

  const shoppingItems = [];

  for (const ing of meal.ingredients) {
    const ingName = ing.name.toLowerCase();
    let foundQuantity = 0;
    
    // Tìm nguyên liệu khớp (chữ hoa/thường)
    const matches = inventory.filter(item => 
      item.name.toLowerCase().includes(ingName) || ingName.includes(item.name.toLowerCase())
    );

    for (const match of matches) {
      foundQuantity += match.quantity;
    }

    if (matches.length === 0 || (ing.quantity && foundQuantity < ing.quantity)) {
      const quantityToBuy = ing.quantity ? Math.max(0, ing.quantity - foundQuantity) : null;
      shoppingItems.push({
        userId,
        itemName: ing.name,
        quantity: quantityToBuy,
        unit: ing.unit,
        mealId: meal.id,
      });
    }
  }

  // Tạo các món còn thiếu vào ShoppingList
  if (shoppingItems.length > 0) {
    await prisma.shoppingList.createMany({
      data: shoppingItems,
    });
  }

  // Chuyển Meal sang ACCEPTED
  const updatedMeal = await prisma.meal.update({
    where: { id: mealId },
    data: { status: MealStatus.ACCEPTED },
  });

  return updatedMeal;
}
