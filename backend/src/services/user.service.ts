import type { Prisma } from '@prisma/client';
import { prisma } from '../lib/prisma.js';
import { userPublic } from './auth.service.js';
import type { UpdateProfileBodyDto } from '../dto/user.dto.js';

export async function updateProfile(userId: string, input: UpdateProfileBodyDto) {
  const data: Prisma.UserUpdateInput = {};
  if (input.name !== undefined) data.name = input.name;
  if (input.healthGoal !== undefined) data.healthGoal = input.healthGoal;
  if (input.fcmTokens !== undefined) data.fcmTokens = input.fcmTokens;

  const user = await prisma.user.update({
    where: { id: userId },
    data,
  });
  return userPublic(user);
}
