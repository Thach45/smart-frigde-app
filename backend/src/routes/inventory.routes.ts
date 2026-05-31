import { Router } from 'express';
import { inventoryController } from '../controllers/inventory.controller.js';
import { requireAuth } from '../middleware/auth.middleware.js';

export const inventoryRouter = Router();

inventoryRouter.use(requireAuth);

inventoryRouter.post('/', inventoryController.create);
inventoryRouter.get('/', inventoryController.getAll);
inventoryRouter.get('/waste/stats', inventoryController.getWasteStats);
inventoryRouter.get('/:id', inventoryController.getOne);
inventoryRouter.patch('/:id', inventoryController.update);
inventoryRouter.delete('/:id', inventoryController.delete);
