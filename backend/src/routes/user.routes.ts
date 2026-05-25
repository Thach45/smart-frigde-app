import { Router } from 'express';

import { userController } from '../controllers/user.controller.js';
import { requireAuth } from '../middleware/auth.middleware.js';

export const userRouter = Router();

userRouter.patch('/me', requireAuth, userController.patchMe);
