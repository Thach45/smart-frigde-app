import type { Request, Response } from 'express';
import { suggestMeal, suggestMealFromItem } from '../services/meal-ai.service.js';
import { acceptMealAndGenerateShoppingList } from '../services/shopping-list.service.js';
import { SuggestMealFromItemSchema } from '../dto/meal.dto.js';

export const mealController = {
  suggest: async (req: Request, res: Response): Promise<void> => {
    try {
      const meal = await suggestMeal(req.userId!);
      res.status(201).json(meal);
    } catch (err: any) {
      console.error('AI Error:', err);
      res.status(500).json({ error: 'Failed to suggest meal', details: err.message });
    }
  },

  suggestFromItem: async (req: Request, res: Response): Promise<void> => {
    const parsed = SuggestMealFromItemSchema.safeParse(req.body);
    if (!parsed.success) {
      res.status(400).json({ error: 'Invalid body', code: 'VALIDATION' });
      return;
    }
    try {
      const meal = await suggestMealFromItem(req.userId!, parsed.data.targetItemId);
      res.status(201).json(meal);
    } catch (err: any) {
      console.error('AI Error:', err);
      res.status(500).json({ error: 'Failed to suggest meal', details: err.message });
    }
  },

  accept: async (req: Request, res: Response): Promise<void> => {
    try {
      const meal = await acceptMealAndGenerateShoppingList(req.userId!, req.params.id as string);
      res.status(200).json(meal);
    } catch (err: any) {
      res.status(400).json({ error: err.message });
    }
  }
};
