import { Router } from 'express';
import { mealController } from '../controllers/meal.controller.js';
import { requireAuth } from '../middleware/auth.middleware.js';

export const mealRouter = Router();

mealRouter.use(requireAuth);

mealRouter.post('/suggest', mealController.suggest);
mealRouter.post('/suggest-from-item', mealController.suggestFromItem);
mealRouter.post('/:id/accept', mealController.accept);
mealRouter.get('/', mealController.getAll);
mealRouter.get('/:id', mealController.getOne);
