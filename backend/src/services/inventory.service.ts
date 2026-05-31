import type { Prisma } from '@prisma/client';
import { EntryMethod } from '@prisma/client';
import { prisma } from '../lib/prisma.js';
import type { CreateInventoryItemDto, UpdateInventoryItemDto } from '../dto/inventory.dto.js';

export async function createItem(userId: string, input: CreateInventoryItemDto) {
  return prisma.inventoryItem.create({
    data: {
      userId,
      name: input.name,
      barcode: input.barcode,
      quantity: input.quantity,
      unit: input.unit,
      category: input.category,
      compartment: input.compartment,
      imageUrl: input.imageUrl || null,
      expiryDate: new Date(input.expiryDate),
      notes: input.notes || null,
      price: input.price !== undefined ? input.price : null,
      kcal: input.kcal !== undefined ? input.kcal : null,
      entryMethod: EntryMethod.MANUAL,
      isExpiryPredicted: false,
    },
  });
}

export async function getItemsByUser(userId: string) {
  return prisma.inventoryItem.findMany({
    where: { userId },
    orderBy: { expiryDate: 'asc' }, // Sort by nearest expiry first
  });
}

export async function getItemById(userId: string, itemId: string) {
  return prisma.inventoryItem.findFirst({
    where: { id: itemId, userId },
  });
}

export async function updateItem(userId: string, itemId: string, input: UpdateInventoryItemDto) {
  const existing = await getItemById(userId, itemId);
  if (!existing) return null;

  const data: Prisma.InventoryItemUpdateInput = {};
  if (input.name !== undefined) data.name = input.name;
  if (input.barcode !== undefined) data.barcode = input.barcode;
  if (input.quantity !== undefined) data.quantity = input.quantity;
  if (input.unit !== undefined) data.unit = input.unit;
  if (input.category !== undefined) data.category = input.category;
  if (input.compartment !== undefined) data.compartment = input.compartment;
  if (input.imageUrl !== undefined) data.imageUrl = input.imageUrl || null;
  if (input.expiryDate !== undefined) data.expiryDate = new Date(input.expiryDate);
  if (input.notes !== undefined) data.notes = input.notes || null;
  if (input.price !== undefined) data.price = input.price;
  if (input.kcal !== undefined) data.kcal = input.kcal;
  if (input.isWasted !== undefined) data.isWasted = input.isWasted;

  return prisma.inventoryItem.update({
    where: { id: itemId },
    data,
  });
}

export async function deleteItem(userId: string, itemId: string) {
  const existing = await getItemById(userId, itemId);
  if (!existing) return false;

  await prisma.inventoryItem.delete({
    where: { id: itemId },
  });
  return true;
}

export async function getWasteStats(userId: string) {
  const now = new Date();
  const currentDay = now.getDay();
  const diff = now.getDate() - currentDay + (currentDay === 0 ? -6 : 1);
  const startOfThisWeek = new Date(now.setDate(diff));
  startOfThisWeek.setHours(0, 0, 0, 0);

  const startOfLastWeek = new Date(startOfThisWeek);
  startOfLastWeek.setDate(startOfLastWeek.getDate() - 7);

  const items = await prisma.inventoryItem.findMany({
    where: {
      userId,
      isWasted: true,
      updatedAt: { gte: startOfLastWeek }
    }
  });

  const getWeightInKg = (quantity: number, unit: string): number => {
    const u = unit.toLowerCase();
    if (u.includes('kg') || u.includes('kilogram') || u.includes('lít') || u === 'l' || u.includes('liter')) {
      return quantity;
    }
    if (u.includes('g') || u.includes('gam') || u.includes('gram') || u.includes('ml')) {
      return quantity / 1000;
    }
    return quantity * 0.15;
  };

  let thisWeekWeight = 0;
  let lastWeekWeight = 0;

  for (const item of items) {
    const weight = getWeightInKg(item.quantity, item.unit);
    if (item.updatedAt >= startOfThisWeek) {
      thisWeekWeight += weight;
    } else {
      lastWeekWeight += weight;
    }
  }

  let percentChange = 0;
  if (lastWeekWeight > 0) {
    percentChange = ((thisWeekWeight - lastWeekWeight) / lastWeekWeight) * 100;
  } else if (thisWeekWeight > 0) {
    percentChange = 100;
  }

  return {
    thisWeekWeight: parseFloat(thisWeekWeight.toFixed(2)),
    lastWeekWeight: parseFloat(lastWeekWeight.toFixed(2)),
    percentChange: Math.round(percentChange)
  };
}
