import { PrismaClient, Compartment, EntryMethod } from '@prisma/client';
import bcrypt from 'bcryptjs';

const prisma = new PrismaClient();
const BCRYPT_ROUNDS = 12;

async function seedUserAndInventory(email: string, name: string) {
  const password = 'password123';
  console.log(`Seeding user: ${email}...`);

  // 1. Find or create user
  let user = await prisma.user.findUnique({
    where: { email },
  });

  if (!user) {
    const passwordHash = await bcrypt.hash(password, BCRYPT_ROUNDS);
    user = await prisma.user.create({
      data: {
        email,
        passwordHash,
        name,
      },
    });
  }

  const userId = user.id;

  // 2. Clear old inventory items for this user
  await prisma.inventoryItem.deleteMany({
    where: { userId },
  });

  // Helper for dates relative to today
  const getRelativeDate = (days: number) => {
    const d = new Date();
    d.setDate(d.getDate() + days);
    return d;
  };

  // 3. Create 12 items
  const items = [
    {
      userId,
      name: 'Thịt bò Mỹ',
      quantity: 0.5,
      unit: 'kg',
      category: 'Thịt cá',
      compartment: Compartment.FREEZER,
      imageUrl: 'https://images.unsplash.com/photo-1544025162-d76694265947?q=80&w=200&auto=format&fit=crop',
      expiryDate: getRelativeDate(30),
      isExpiryPredicted: false,
      entryMethod: EntryMethod.MANUAL,
      price: 180000,
      kcal: 1250,
      notes: 'Thịt ba chỉ bò mua siêu thị WinMart',
    },
    {
      userId,
      name: 'Cá hồi phi lê',
      quantity: 0.3,
      unit: 'kg',
      category: 'Thịt cá',
      compartment: Compartment.FREEZER,
      imageUrl: 'https://images.unsplash.com/photo-1519708227418-c8fd9a32b7a2?q=80&w=200&auto=format&fit=crop',
      expiryDate: getRelativeDate(1),
      isExpiryPredicted: false,
      entryMethod: EntryMethod.MANUAL,
      price: 120000,
      kcal: 600,
      notes: 'Nấu áp chảo sốt chanh leo',
    },
    {
      userId,
      name: 'Cà chua bi',
      quantity: 500,
      unit: 'g',
      category: 'Rau củ',
      compartment: Compartment.CRISPER,
      imageUrl: 'https://images.unsplash.com/photo-1595855759920-86582396756a?q=80&w=200&auto=format&fit=crop',
      expiryDate: getRelativeDate(-2),
      isExpiryPredicted: false,
      entryMethod: EntryMethod.MANUAL,
      price: 25000,
      kcal: 90,
      notes: 'Làm salad hoặc nấu canh',
    },
    {
      userId,
      name: 'Xà lách thủy canh',
      quantity: 1,
      unit: 'bó',
      category: 'Rau củ',
      compartment: Compartment.CRISPER,
      imageUrl: 'https://images.unsplash.com/photo-1556801712-76c8eb07bbc9?q=80&w=200&auto=format&fit=crop',
      expiryDate: getRelativeDate(2),
      isExpiryPredicted: false,
      entryMethod: EntryMethod.MANUAL,
      price: 15000,
      kcal: 40,
      notes: 'Nên ăn sớm kẻo héo úa',
    },
    {
      userId,
      name: 'Trứng gà ta',
      quantity: 10,
      unit: 'quả',
      category: 'Sữa trứng',
      compartment: Compartment.DOOR,
      imageUrl: 'https://images.unsplash.com/photo-1516448620398-c5f44bf9f441?q=80&w=200&auto=format&fit=crop',
      expiryDate: getRelativeDate(10),
      isExpiryPredicted: false,
      entryMethod: EntryMethod.MANUAL,
      price: 35000,
      kcal: 700,
      notes: 'Trứng gà quê mẹ gửi lên',
    },
    {
      userId,
      name: 'Sữa tươi TH True Milk',
      quantity: 1,
      unit: 'lít',
      category: 'Sữa trứng',
      compartment: Compartment.DOOR,
      imageUrl: 'https://images.unsplash.com/photo-1563636619-e9143da7973b?q=80&w=200&auto=format&fit=crop',
      expiryDate: getRelativeDate(3),
      isExpiryPredicted: false,
      entryMethod: EntryMethod.MANUAL,
      price: 32000,
      kcal: 600,
      notes: 'Đã mở nắp ngày hôm qua, uống trong 3 ngày',
    },
    {
      userId,
      name: 'Tương ớt Chinsu',
      quantity: 1,
      unit: 'chai',
      category: 'Gia vị',
      compartment: Compartment.DOOR,
      imageUrl: 'https://images.unsplash.com/photo-1618413997752-601e3b5df510?q=80&w=200&auto=format&fit=crop',
      expiryDate: getRelativeDate(180),
      isExpiryPredicted: false,
      entryMethod: EntryMethod.MANUAL,
      price: 18000,
      kcal: 250,
      notes: 'Để ở cánh tủ mát',
    },
    {
      userId,
      name: 'Bơ sáp Đắk Lắk',
      quantity: 3,
      unit: 'quả',
      category: 'Trái cây',
      compartment: Compartment.FRIDGE,
      imageUrl: 'https://images.unsplash.com/photo-1523049673857-eb18f1d7b578?q=80&w=200&auto=format&fit=crop',
      expiryDate: getRelativeDate(2),
      isExpiryPredicted: false,
      entryMethod: EntryMethod.MANUAL,
      price: 45000,
      kcal: 480,
      notes: 'Quả chín mềm tay rồi, cần làm sinh tố ngay',
    },
    {
      userId,
      name: 'Táo Envy',
      quantity: 4,
      unit: 'quả',
      category: 'Trái cây',
      compartment: Compartment.FRIDGE,
      imageUrl: 'https://images.unsplash.com/photo-1560806887-1e4cd0b6cbd6?q=80&w=200&auto=format&fit=crop',
      expiryDate: getRelativeDate(7),
      isExpiryPredicted: false,
      entryMethod: EntryMethod.MANUAL,
      price: 80000,
      kcal: 320,
      notes: 'Rất giòn ngọt',
    },
    {
      userId,
      name: 'Bia Heineken',
      quantity: 6,
      unit: 'lon',
      category: 'Đồ uống',
      compartment: Compartment.FRIDGE,
      imageUrl: 'https://images.unsplash.com/photo-1608270586620-248524c67de9?q=80&w=200&auto=format&fit=crop',
      expiryDate: getRelativeDate(120),
      isExpiryPredicted: false,
      entryMethod: EntryMethod.MANUAL,
      price: 115000,
      kcal: 900,
      notes: 'Đồ nhậu cuối tuần cùng bạn bè',
    },
    {
      userId,
      name: 'Coca Cola Zero',
      quantity: 4,
      unit: 'lon',
      category: 'Đồ uống',
      compartment: Compartment.FRIDGE,
      imageUrl: 'https://images.unsplash.com/photo-1622483767028-3f66f32aef97?q=80&w=200&auto=format&fit=crop',
      expiryDate: getRelativeDate(90),
      isExpiryPredicted: false,
      entryMethod: EntryMethod.MANUAL,
      price: 40000,
      kcal: 0,
      notes: 'Không calo giải khát',
    },
    {
      userId,
      name: 'Nấm đùi gà',
      quantity: 1,
      unit: 'hộp',
      category: 'Rau củ',
      compartment: Compartment.CRISPER,
      imageUrl: 'https://images.unsplash.com/photo-1605389659020-f14d8ff2f592?q=80&w=200&auto=format&fit=crop',
      expiryDate: getRelativeDate(-1),
      isExpiryPredicted: false,
      entryMethod: EntryMethod.MANUAL,
      price: 28000,
      kcal: 120,
      notes: 'Hơi thâm nấm rồi, nên kiểm tra kỹ trước khi ăn',
    }
  ];

  console.log(`Inserting 12 items for ${email}...`);
  for (const item of items) {
    await prisma.inventoryItem.create({
      data: item,
    });
  }
}

async function main() {
  console.log(`Seeding database for test users...`);
  await seedUserAndInventory('test@gmail.com', 'Test User');
  await seedUserAndInventory('admin@gmail.com', 'Admin User');
  console.log(`All seed data written successfully!`);
}

main()
  .catch((e) => {
    console.error(e);
    process.exit(1);
  })
  .finally(async () => {
    await prisma.$disconnect();
  });
