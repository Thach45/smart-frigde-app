import type { Request, Response } from 'express';
import { prisma } from '../lib/prisma.js';

export const shoppingListController = {
  getAll: async (req: Request, res: Response): Promise<void> => {
    try {
      const items = await prisma.shoppingList.findMany({
        where: { userId: req.userId! },
        orderBy: { createdAt: 'desc' }
      });
      res.status(200).json(items);
    } catch (err: any) {
      console.error('Get shopping list error:', err);
      res.status(500).json({ error: 'Failed to retrieve shopping list' });
    }
  },

  create: async (req: Request, res: Response): Promise<void> => {
    try {
      const { itemName, quantity, unit, mealId } = req.body;
      if (!itemName) {
        res.status(400).json({ error: 'Item name is required' });
        return;
      }

      const item = await prisma.shoppingList.create({
        data: {
          userId: req.userId!,
          itemName,
          quantity: quantity ? parseFloat(quantity) : null,
          unit: unit || null,
          isPurchased: false,
          mealId: mealId || null
        }
      });
      res.status(201).json(item);
    } catch (err: any) {
      console.error('Create shopping item error:', err);
      res.status(500).json({ error: 'Failed to create shopping item', details: err.message });
    }
  },

  toggle: async (req: Request, res: Response): Promise<void> => {
    try {
      const id = req.params.id as string;
      const { isPurchased } = req.body;

      const item = await prisma.shoppingList.findFirst({
        where: { id, userId: req.userId! }
      });

      if (!item) {
        res.status(404).json({ error: 'Shopping item not found' });
        return;
      }

      const updated = await prisma.shoppingList.update({
        where: { id },
        data: { isPurchased: isPurchased ?? !item.isPurchased }
      });

      res.status(200).json(updated);
    } catch (err: any) {
      console.error('Toggle shopping item error:', err);
      res.status(500).json({ error: 'Failed to update shopping item', details: err.message });
    }
  },

  delete: async (req: Request, res: Response): Promise<void> => {
    try {
      const id = req.params.id as string;

      const item = await prisma.shoppingList.findFirst({
        where: { id, userId: req.userId! }
      });

      if (!item) {
        res.status(404).json({ error: 'Shopping item not found' });
        return;
      }

      await prisma.shoppingList.delete({
        where: { id }
      });

      res.status(200).json({ message: 'Shopping item deleted successfully' });
    } catch (err: any) {
      console.error('Delete shopping item error:', err);
      res.status(500).json({ error: 'Failed to delete shopping item', details: err.message });
    }
  }
};
