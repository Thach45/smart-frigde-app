import { z } from 'zod';

export const SuggestMealFromItemSchema = z.object({
  targetItemId: z.string().min(1),
});

export type SuggestMealFromItemDto = z.infer<typeof SuggestMealFromItemSchema>;
