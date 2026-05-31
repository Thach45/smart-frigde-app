import type { Request, Response } from 'express';
import { suggestMeal, suggestMealFromItem } from '../services/meal-ai.service.js';
import { acceptMealAndGenerateShoppingList } from '../services/shopping-list.service.js';
import { SuggestMealFromItemSchema } from '../dto/meal.dto.js';
import { prisma } from '../lib/prisma.js';

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
  },

  getAll: async (req: Request, res: Response): Promise<void> => {
    try {
      const meals = await prisma.meal.findMany({
        where: { userId: req.userId! },
        orderBy: { date: 'desc' }
      });
      res.status(200).json(meals);
    } catch (err: any) {
      res.status(500).json({ error: 'Failed to retrieve meals' });
    }
  },

  getOne: async (req: Request, res: Response): Promise<void> => {
    try {
      const meal = await prisma.meal.findFirst({
        where: { id: req.params.id as string, userId: req.userId! }
      });
      if (!meal) {
        res.status(404).json({ error: 'Meal not found' });
        return;
      }
      res.status(200).json(meal);
    } catch (err: any) {
      res.status(500).json({ error: 'Failed to retrieve meal details' });
    }
  },

  cook: async (req: Request, res: Response): Promise<void> => {
    try {
      const mealId = req.params.id as string;
      const meal = await prisma.meal.findFirst({
        where: { id: mealId, userId: req.userId! }
      });
      if (!meal) {
        res.status(404).json({ error: 'Meal not found' });
        return;
      }
      const updatedMeal = await prisma.meal.update({
        where: { id: mealId },
        data: { status: 'COOKED' }
      });
      res.status(200).json(updatedMeal);
    } catch (err: any) {
      console.error('Cook Error:', err);
      res.status(500).json({ error: 'Failed to mark meal as cooked', details: err.message });
    }
  },

  getWeeklyCalories: async (req: Request, res: Response): Promise<void> => {
    try {
      const startDateStr = req.query.startDate as string;
      if (!startDateStr) {
        res.status(400).json({ error: 'startDate parameter is required' });
        return;
      }
      
      const start = new Date(startDateStr);
      const end = new Date(start);
      end.setDate(end.getDate() + 7);
      
      const meals = await prisma.meal.findMany({
        where: {
          userId: req.userId!,
          status: 'COOKED',
          date: {
            gte: start,
            lt: end
          }
        },
        select: {
          date: true,
          calories: true
        }
      });
      
      // Group and sum calories by date string (YYYY-MM-DD)
      const dailyCaloriesMap: { [dateStr: string]: number } = {};
      
      // Initialize the 7 days of the week with 0 calories
      for (let i = 0; i < 7; i++) {
        const d = new Date(start);
        d.setDate(d.getDate() + i);
        const yyyy = d.getFullYear();
        const mm = String(d.getMonth() + 1).padStart(2, '0');
        const dd = String(d.getDate()).padStart(2, '0');
        dailyCaloriesMap[`${yyyy}-${mm}-${dd}`] = 0;
      }
      
      for (const meal of meals) {
        const mealDate = new Date(meal.date);
        const yyyy = mealDate.getFullYear();
        const mm = String(mealDate.getMonth() + 1).padStart(2, '0');
        const dd = String(mealDate.getDate()).padStart(2, '0');
        const dateKey = `${yyyy}-${mm}-${dd}`;
        
        if (dateKey in dailyCaloriesMap) {
          dailyCaloriesMap[dateKey] += meal.calories || 0;
        }
      }
      
      const result = Object.entries(dailyCaloriesMap).map(([date, totalCalories]) => ({
        date,
        totalCalories
      }));
      
      res.status(200).json(result);
    } catch (err: any) {
      console.error('Weekly calories error:', err);
      res.status(500).json({ error: 'Failed to retrieve weekly calories', details: err.message });
    }
  },

  voiceAssistant: async (req: Request, res: Response): Promise<void> => {
    try {
      const { text } = req.body;
      console.log('Voice Assistant Text Command:', text);
      res.status(200).json({ reply: `Đã nhận được giọng nói: "${text}"` });
    } catch (err: any) {
      console.error('Voice Assistant Error:', err);
      res.status(500).json({ error: 'Failed to process voice assistant command' });
    }
  }
};
