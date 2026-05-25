Tài Liệu Đặc Tả Chức Năng (Functional Spec)

Dự án: SmartBite Mobile - Ứng dụng Quản lý Tủ lạnh Thông minh
Phiên bản: 2.0 (Functional Focus)
Mục tiêu: Trợ lý quản lý thực phẩm cá nhân, chống lãng phí và tối ưu nấu nướng.

1. Nhóm Quản Lý Kho Thực Phẩm (Smart Inventory)

1.1. Quét Hình Ảnh & Mã Vạch (Smart Scanner)

Chức năng là gì? Trợ lý nhập liệu tự động bằng Camera.

Làm gì? Dùng camera điện thoại quét mã vạch trên bao bì hoặc chụp ảnh mâm đồ ăn để tự động nhận diện tên món, phân loại và số lượng.

Vì sao cần? Người dùng rất lười nhập tay từng món. Tốc độ nhập liệu quyết định sự thành bại của một ứng dụng quản lý kho.

Điểm đặc biệt: Không cần gõ phím. Trí tuệ nhân tạo (AI) tự động đoán luôn hạn sử dụng dựa trên loại thực phẩm và hình ảnh nhận diện được.

Flow hoạt động: Người dùng mở ứng dụng -> Bấm nút Camera to ở giữa màn hình -> Đưa mã vạch hoặc hình ảnh thực phẩm vào khung ngắm -> Ứng dụng xử lý trong 1 giây -> Tự động thêm món đồ đó vào danh sách tủ lạnh.

1.2. Phân Loại Kép (Ngăn Tủ & Danh Mục)

Chức năng là gì? Hệ thống sắp xếp thực phẩm thông minh.

Làm gì? Chia đồ ăn theo vị trí thực tế của tủ lạnh (Ngăn đá, Ngăn mát, Cánh tủ) kết hợp với thuộc tính thực phẩm (Thịt, Rau củ, Trái cây).

Vì sao cần? Khớp với thói quen tìm đồ ngoài đời thực của người dùng. Đồng thời giúp AI tính hạn sử dụng chuẩn xác (đồ để ngăn đá sẽ lâu hỏng hơn ngăn mát).

Điểm đặc biệt: Người dùng chỉ cần làm một việc dễ nhất là "chọn vị trí cất", còn việc "phân loại danh mục" AI sẽ tự động gán nhãn ngầm.

Flow hoạt động: Quét/Nhập đồ thành công -> Ứng dụng hỏi "Bạn cất ở đâu?" -> Người dùng bấm chọn "Ngăn mát" -> Món đồ lập tức xuất hiện ở tab Ngăn mát trên màn hình chính.

2. Nhóm Trợ Lý Thông Minh (Smart Assistants)

2.1. Trợ Lý Giọng Nói (Voice Assistant)

Chức năng là gì? Công cụ tương tác rảnh tay.

Làm gì? Nghe và hiểu giọng nói của người dùng để kiểm tra đồ đạc hoặc cập nhật số lượng đồ trong tủ.

Vì sao cần? Vô cùng tiện lợi khi người dùng đang đẩy xe trong siêu thị cần tra cứu nhanh, hoặc khi tay đang ướt lúc nấu ăn không thể bấm điện thoại.

Điểm đặc biệt: Hiểu ngôn ngữ giao tiếp tự nhiên thay vì câu lệnh máy móc (VD: "Hôm qua mẹ mua mấy quả trứng rồi nhỉ?").

Flow hoạt động: Nhấn giữ biểu tượng Micro trên màn hình -> Nói câu hỏi -> AI phân tích ý định -> Ứng dụng phản hồi lại bằng văn bản trên màn hình (hoặc phát ra âm thanh trả lời).

2.2. Nút Cứu Trợ: "Nấu Gì Hôm Nay?" (1-Tap Recipe)

Chức năng là gì? Máy phát công thức nấu ăn "dọn tủ".

Làm gì? Tự động đề xuất các món ăn ngon dựa trên những nguyên liệu sắp hỏng đang có sẵn trong tủ lạnh.

Vì sao cần? Giải quyết câu hỏi đau đầu nhất mỗi chiều đi làm về, đồng thời triệt tiêu trực tiếp vấn đề lãng phí thực phẩm do để quên đồ ăn.

Điểm đặc biệt: Chỉ gợi ý những món có thể nấu ngay với đồ đang có, giao diện hiển thị dạng thẻ vuốt rất trực quan và vui nhộn.

Flow hoạt động: Bấm nút "Nấu Gì" -> Ứng dụng tự lọc các món sắp hết hạn -> Hiện ra các thẻ hình ảnh món ăn -> Người dùng vuốt sang trái (bỏ qua) hoặc vuốt sang phải (lưu lại) -> Bấm vào để xem từng bước nấu.

3. Nhóm Sức Khỏe & Nhắc Nhở (Health & Alerts)

3.1. Kế Hoạch Dinh Dưỡng Cá Nhân (Diet & Meal Planner)

Chức năng là gì? Huấn luyện viên ăn uống cá nhân hóa.

Làm gì? Lên thực đơn các bữa ăn trong tuần dựa trên mục tiêu sức khỏe cá nhân (giảm cân, tăng cơ, giữ dáng).

Vì sao cần? Gắn liền việc quản lý đồ ăn với mục tiêu sức khỏe, tạo động lực cực lớn để người dùng mở ứng dụng mỗi ngày.

Điểm đặc biệt: AI sẽ tự động thiết kế thực đơn sao cho ưu tiên dùng tối đa các nguyên liệu đang có trong tủ lạnh. Nếu thiếu món gì, nó tự động ném món đó sang "Danh sách đi chợ".

Flow hoạt động: Người dùng nhập mục tiêu (VD: Giảm cân) -> Ứng dụng sinh ra thực đơn tuần -> Hiển thị danh sách món ăn cho Sáng/Trưa/Tối mỗi ngày -> Tự động đẩy đồ còn thiếu vào danh sách mua sắm.

3.2. Cảnh Báo Hết Hạn Thông Minh (Expiry Alerts)

Chức năng là gì? Hệ thống báo thức cho thực phẩm.

Làm gì? Gửi thông báo đẩy (Push Notification) lên màn hình khóa điện thoại để nhắc nhở về các món đồ sắp hỏng trước 1-2 ngày.

Vì sao cần? Tránh việc vứt bỏ thức ăn và lãng phí tiền bạc chỉ vì quên đồ ở góc khuất tủ lạnh.

Điểm đặc biệt: Chỉ nhắc đúng lúc, không làm phiền. Có thể kết hợp nhắc nhở kèm theo gợi ý món ăn có thể nấu để xử lý ngay món đồ đó.

Flow hoạt động: Ứng dụng chạy ngầm đếm ngược ngày -> Đúng khung giờ vàng (VD: 17h00) -> Hiện thông báo trên điện thoại: "500g thịt bò sắp hỏng, nấu ngay thôi!" -> Bấm vào thông báo sẽ mở ra màn hình hướng dẫn nấu món thịt bò.