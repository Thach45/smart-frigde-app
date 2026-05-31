import type { Request, Response } from 'express';
import { CreateInventoryItemSchema, UpdateInventoryItemSchema } from '../dto/inventory.dto.js';
import * as inventoryService from '../services/inventory.service.js';

const validationJson = { error: 'Invalid body', code: 'VALIDATION' } as const;

export const inventoryController = {
  create: async (req: Request, res: Response): Promise<void> => {
    const parsed = CreateInventoryItemSchema.safeParse(req.body);
    if (!parsed.success) {
      res.status(400).json(validationJson);
      return;
    }
    try {
      const item = await inventoryService.createItem(req.userId!, parsed.data);
      res.status(201).json(item);
    } catch (err: any) {
      res.status(500).json({ error: 'Failed to create item', details: err.message });
    }
  },

  getAll: async (req: Request, res: Response): Promise<void> => {
    try {
      const items = await inventoryService.getItemsByUser(req.userId!);
      res.status(200).json(items);
    } catch (err: any) {
      res.status(500).json({ error: 'Failed to get items' });
    }
  },

  getOne: async (req: Request, res: Response): Promise<void> => {
    try {
      const item = await inventoryService.getItemById(req.userId!, req.params.id as string);
      if (!item) {
        res.status(404).json({ error: 'Item not found' });
        return;
      }
      res.status(200).json(item);
    } catch (err: any) {
      res.status(500).json({ error: 'Failed to get item' });
    }
  },

  update: async (req: Request, res: Response): Promise<void> => {
    const parsed = UpdateInventoryItemSchema.safeParse(req.body);
    if (!parsed.success) {
      res.status(400).json(validationJson);
      return;
    }
    try {
      const item = await inventoryService.updateItem(req.userId!, req.params.id as string, parsed.data);
      if (!item) {
        res.status(404).json({ error: 'Item not found' });
        return;
      }
      res.status(200).json(item);
    } catch (err: any) {
      res.status(500).json({ error: 'Failed to update item' });
    }
  },

  delete: async (req: Request, res: Response): Promise<void> => {
    try {
      const success = await inventoryService.deleteItem(req.userId!, req.params.id as string);
      if (!success) {
        res.status(404).json({ error: 'Item not found' });
        return;
      }
      res.status(204).send();
    } catch (err: any) {
      res.status(500).json({ error: 'Failed to delete item' });
    }
  },

  getWasteStats: async (req: Request, res: Response): Promise<void> => {
    try {
      const stats = await inventoryService.getWasteStats(req.userId!);
      res.status(200).json(stats);
    } catch (err: any) {
      console.error('Get waste stats error:', err);
      res.status(500).json({ error: 'Failed to retrieve waste stats' });
    }
  },
};
