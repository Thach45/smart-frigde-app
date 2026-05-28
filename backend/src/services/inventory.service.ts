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
