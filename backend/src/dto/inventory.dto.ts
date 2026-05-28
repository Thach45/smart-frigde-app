import { z } from 'zod';
import { Compartment } from '@prisma/client';

export const CreateInventoryItemSchema = z.object({
  name: z.string().min(1).max(200),
  barcode: z.string().optional(),
  quantity: z.number().positive(),
  unit: z.string().min(1).max(50),
  category: z.string().min(1).max(100),
  compartment: z.nativeEnum(Compartment),
  imageUrl: z.string().url().optional().or(z.string().length(0).optional()),
  expiryDate: z.string().datetime(), // Expecting ISO-8601 string
  notes: z.string().optional(),
  price: z.number().nonnegative().optional(),
  kcal: z.number().int().nonnegative().optional(),
});

export type CreateInventoryItemDto = z.infer<typeof CreateInventoryItemSchema>;

export const UpdateInventoryItemSchema = z.object({
  name: z.string().min(1).max(200).optional(),
  barcode: z.string().optional(),
  quantity: z.number().positive().optional(),
  unit: z.string().min(1).max(50).optional(),
  category: z.string().min(1).max(100).optional(),
  compartment: z.nativeEnum(Compartment).optional(),
  imageUrl: z.string().url().optional().or(z.string().length(0).optional()),
  expiryDate: z.string().datetime().optional(),
  notes: z.string().optional(),
  price: z.number().nonnegative().optional(),
  kcal: z.number().int().nonnegative().optional(),
  isWasted: z.boolean().optional(),
});

export type UpdateInventoryItemDto = z.infer<typeof UpdateInventoryItemSchema>;
