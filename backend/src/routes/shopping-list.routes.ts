import { Router } from 'express';
import { shoppingListController } from '../controllers/shopping-list.controller.js';
import { requireAuth } from '../middleware/auth.middleware.js';

export const shoppingListRouter = Router();

shoppingListRouter.use(requireAuth);

shoppingListRouter.get('/', shoppingListController.getAll);
shoppingListRouter.post('/', shoppingListController.create);
shoppingListRouter.patch('/:id', shoppingListController.toggle);
shoppingListRouter.delete('/:id', shoppingListController.delete);
