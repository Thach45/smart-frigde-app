import { PrismaClient } from '@prisma/client';

const prisma = new PrismaClient();

async function main() {
  const user = await prisma.user.findUnique({
    where: { email: 'admin@gmail.com' }
  });

  if (!user) {
    console.error('User test@gmail.com not found. Run npm run db:seed first.');
    return;
  }

  const userId = user.id;

  // Let's create or update items to be wasted
  // 1. Wasted item for this week (e.g. Cà chua bi, 500g = 0.5kg)
  const itemThisWeek = await prisma.inventoryItem.findFirst({
    where: { userId, name: 'Cà chua bi' }
  });

  if (itemThisWeek) {
    await prisma.inventoryItem.update({
      where: { id: itemThisWeek.id },
      data: {
        isWasted: true,
        updatedAt: new Date() // Today (This Week)
      }
    });
    console.log(`Updated ${itemThisWeek.name} (500g) as wasted this week.`);
  }

  // 2. Wasted item for last week (e.g. Bơ sáp Đắk Lắk, 3 quả = 3 * 0.15 = 0.45kg)
  const itemLastWeek = await prisma.inventoryItem.findFirst({
    where: { userId, name: 'Bơ sáp Đắk Lắk' }
  });

  if (itemLastWeek) {
    const eightDaysAgo = new Date();
    eightDaysAgo.setDate(eightDaysAgo.getDate() - 8);

    await prisma.inventoryItem.update({
      where: { id: itemLastWeek.id },
      data: {
        isWasted: true,
        updatedAt: eightDaysAgo // 8 days ago (Last Week)
      }
    });
    console.log(`Updated ${itemLastWeek.name} (3 units) as wasted last week.`);
  }
}

main()
  .catch((e) => {
    console.error(e);
    process.exit(1);
  })
  .finally(async () => {
    await prisma.$disconnect();
  });
