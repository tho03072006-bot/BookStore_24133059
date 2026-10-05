/* =====================================================================
   ĐỀ SỐ 02 - Dữ liệu mẫu để test (đề ghi: "dữ liệu tự thêm vào").
   Sinh viên: Trần Minh Thọ - MSSV: 24133059

   Chạy file 01_schema.sql TRƯỚC, rồi mới chạy file này.
   Nội dung: 5 tác giả, 50 cuốn sách (10 cuốn / tác giả -> đúng 4 trang
   khi phân trang 3 sách/trang ở Câu 3), 4 tài khoản và 20 review.

   Mật khẩu của cả 4 tài khoản đều là 123456 (đã băm MD5 - cột passwd
   varchar(32) vừa đúng 32 ký tự hex).
   ===================================================================== */

USE BookStore;
GO

/* Xoá dữ liệu cũ để chạy lại file này nhiều lần vẫn ra kết quả giống nhau. */
DELETE FROM rating;
DELETE FROM book_author;
DELETE FROM books;
DELETE FROM author;
DELETE FROM users;
GO

/* --------------------------- TÁC GIẢ ---------------------------
   Cột ID vẫn là IDENTITY (tăng tự động) đúng như đề yêu cầu; ở đây
   bật SET IDENTITY_INSERT chỉ để dữ liệu mẫu có ID cố định, nhờ vậy
   bảng liên kết book_author bên dưới luôn trỏ đúng. Sau khi tắt
   IDENTITY_INSERT, SQL Server tự tiếp tục đánh số từ giá trị lớn nhất.
   ---------------------------------------------------------------- */
SET IDENTITY_INSERT author ON;
INSERT INTO author (author_id, author_name, date_of_birth) VALUES
    (1, N'Nguyễn Nhật Ánh', '1955-05-07'),
    (2, N'Tô Hoài', '1920-09-27'),
    (3, N'Haruki Murakami', '1949-01-12'),
    (4, N'Paulo Coelho', '1947-08-24'),
    (5, N'Nguyễn Ngọc Tư', '1976-01-01');
SET IDENTITY_INSERT author OFF;
GO

/* ---------------------------- SÁCH ----------------------------- */
SET IDENTITY_INSERT books ON;
INSERT INTO books (bookid, isbn, title, publisher, price, description, publish_date, cover_image, quantity) VALUES
    (1, 978000137, N'Mắt Biếc', N'NXB Trẻ', 95.00, N'Chuyện tình trong trẻo mà day dứt của Ngạn và Hà Lan, gắn với làng Đo Đo và cây bàng tuổi thơ.', '1990-01-15', 'book_01.webp', 40),
    (2, 978000274, N'Cho Tôi Xin Một Vé Đi Tuổi Thơ', N'NXB Trẻ', 78.50, N'Cuốn sách đưa người lớn trở lại những ngày thơ bé với cách nhìn hồn nhiên và tinh nghịch.', '2008-12-01', 'book_02.webp', 65),
    (3, 978000411, N'Tôi Thấy Hoa Vàng Trên Cỏ Xanh', N'NXB Trẻ', 125.00, N'Nhật ký của cậu bé Thiều ở một làng quê nghèo miền Trung, nơi tình anh em và tình đầu cùng lớn lên.', '2010-12-09', 'book_03.webp', 52),
    (4, 978000548, N'Kính Vạn Hoa', N'NXB Kim Đồng', 145.00, N'Bộ truyện dài nhiều tập về nhóm bạn Quý ròm, Tiểu Long và nhỏ Hạnh với vô vàn tình huống dở khóc dở cười.', '1995-06-20', 'book_04.webp', 30),
    (5, 978000685, N'Cô Gái Đến Từ Hôm Qua', N'NXB Trẻ', 82.00, N'Thư thích Việt An của hiện tại mà không nhận ra cô bé Tiểu Li ngày xưa.', '1989-03-10', 'book_05.webp', 47),
    (6, 978000822, N'Ngồi Khóc Trên Cây', N'NXB Trẻ', 110.00, N'Câu chuyện của Đông và Rùa ở vùng đồi Đo Đo, nơi lòng tốt luôn được đền đáp.', '2013-06-27', 'book_06.webp', 38),
    (7, 978000959, N'Bồ Câu Không Đưa Thư', N'NXB Trẻ', 69.00, N'Bí mật của những lá thư không đề tên trong ngăn bàn một lớp học cấp ba.', '1993-09-05', 'book_07.webp', 55),
    (8, 978001096, N'Còn Chút Gì Để Nhớ', N'NXB Trẻ', 88.00, N'Chuyện tình thời chiến của chàng sinh viên tỉnh lẻ giữa Sài Gòn những năm 70.', '1988-11-11', 'book_08.webp', 42),
    (9, 978001233, N'Hạ Đỏ', N'NXB Trẻ', 72.00, N'Một mùa hè ở quê ngoại với những trận đánh nhau trẻ con và rung động đầu đời.', '1991-05-30', 'book_09.webp', 49),
    (10, 978001370, N'Thằng Quỷ Nhỏ', N'NXB Trẻ', 76.50, N'Quỳnh - cậu bé bị bạn bè trêu chọc vì ngoại hình - và bài học về lòng tử tế.', '1990-08-08', 'book_10.webp', 44),
    (11, 978001507, N'Dế Mèn Phiêu Lưu Ký', N'NXB Kim Đồng', 65.00, N'Hành trình của chàng Dế Mèn từ kiêu căng bồng bột đến trưởng thành, tác phẩm thiếu nhi kinh điển.', '1941-04-01', 'book_11.webp', 80),
    (12, 978001644, N'Vợ Chồng A Phủ', N'NXB Văn Học', 55.00, N'Số phận của Mị và A Phủ dưới ách thống trị của nhà thống lý Pá Tra ở Hồng Ngài.', '1952-07-15', 'book_12.svg', 60),
    (13, 978001781, N'Truyện Tây Bắc', N'NXB Văn Học', 70.00, N'Tập truyện viết về đời sống và cuộc đổi đời của đồng bào vùng cao Tây Bắc.', '1953-01-20', 'book_13.webp', 35),
    (14, 978001918, N'O Chuột', N'NXB Hội Nhà Văn', 58.00, N'Tập truyện loài vật đầu tay thể hiện biệt tài quan sát của Tô Hoài.', '1942-10-10', 'book_14.webp', 28),
    (15, 978002055, N'Nhà Nghèo', N'NXB Hội Nhà Văn', 52.00, N'Những mảnh đời nghèo khó ở ngoại ô Hà Nội trước Cách mạng.', '1944-02-18', 'book_15.webp', 26),
    (16, 978002192, N'Quê Người', N'NXB Văn Học', 63.00, N'Bức tranh làng nghề dệt cửi ven đô đang lụi tàn vì thời cuộc.', '1941-12-05', 'book_16.webp', 31),
    (17, 978002329, N'Cát Bụi Chân Ai', N'NXB Hội Nhà Văn', 98.00, N'Hồi ký chân thực về đời sống văn nghệ sĩ Hà Nội một thời.', '1992-09-09', 'book_17.webp', 33),
    (18, 978002466, N'Chiều Chiều', N'NXB Hội Nhà Văn', 92.00, N'Tập hồi ký tiếp nối Cát Bụi Chân Ai, kể chuyện đi thực tế ở nông thôn.', '1999-04-22', 'book_18.webp', 27),
    (19, 978002603, N'Chuyện Cũ Hà Nội', N'NXB Kim Đồng', 105.00, N'Tập ký ghi lại nếp sống, phố phường và con người Hà Nội xưa.', '1986-10-10', 'book_19.webp', 45),
    (20, 978002740, N'Ba Người Khác', N'NXB Đà Nẵng', 87.00, N'Tiểu thuyết nhìn lại thời kỳ cải cách ruộng đất qua lời kể của một anh đội.', '2006-12-01', 'book_20.webp', 24),
    (21, 978002877, N'Rừng Na Uy', N'NXB Hội Nhà Văn', 135.00, N'Toru Watanabe hồi tưởng tuổi hai mươi ở Tokyo, giữa Naoko và Midori, giữa mất mát và trưởng thành.', '1987-09-04', 'book_21.webp', 50),
    (22, 978003014, N'Kafka Bên Bờ Biển', N'NXB Văn Học', 165.00, N'Hai mạch truyện song song của cậu bé Kafka bỏ nhà đi và ông lão Nakata biết nói chuyện với mèo.', '2002-09-12', 'book_22.webp', 41),
    (23, 978003151, N'Biên Niên Ký Chim Vặn Dây Cót', N'NXB Hội Nhà Văn', 189.00, N'Toru Okada đi tìm con mèo mất tích rồi lạc vào những tầng sâu kỳ lạ của ký ức và lịch sử.', '1994-04-12', 'book_23.webp', 29),
    (24, 978003288, N'1Q84', N'NXB Hội Nhà Văn', 245.00, N'Aomame và Tengo trong một năm 1984 có hai mặt trăng, nơi thực tại tách làm đôi.', '2009-05-29', 'book_24.webp', 22),
    (25, 978003425, N'Người Tình Sputnik', N'NXB Hội Nhà Văn', 112.00, N'Sumire biến mất trên một hòn đảo Hy Lạp, để lại câu hỏi về tình yêu và sự cô độc.', '1999-04-01', 'book_25.webp', 36),
    (26, 978003562, N'Phía Nam Biên Giới, Phía Tây Mặt Trời', N'NXB Hội Nhà Văn', 108.00, N'Hajime gặp lại mối tình thơ ấu Shimamoto khi đã có gia đình yên ấm.', '1992-10-05', 'book_26.webp', 39),
    (27, 978003699, N'Xứ Sở Diệu Kỳ Tàn Bạo Và Chốn Tận Cùng Thế Giới', N'NXB Hội Nhà Văn', 175.00, N'Hai thế giới song song: một Tokyo ngầm đầy bạo lực và một thị trấn có những con kỳ lân.', '1985-06-15', 'book_27.webp', 25),
    (28, 978003836, N'Nhảy Nhảy Nhảy', N'NXB Hội Nhà Văn', 142.00, N'Nhân vật tôi trở lại khách sạn Cá Heo để nối lại sợi dây với quá khứ.', '1988-10-24', 'book_28.webp', 28),
    (29, 978003973, N'Tazaki Tsukuru Không Màu Và Những Năm Tháng Hành Hương', N'NXB Hội Nhà Văn', 155.00, N'Tsukuru đi tìm lý do vì sao bốn người bạn thân cắt đứt với mình mười sáu năm trước.', '2013-04-12', 'book_29.webp', 34),
    (30, 978004110, N'Lắng Nghe Gió Hát', N'NXB Hội Nhà Văn', 95.00, N'Tác phẩm đầu tay, mở đầu bộ ba Chuột với giọng văn nhẹ như một mùa hè.', '1979-07-01', 'book_30.webp', 30),
    (31, 978004247, N'Nhà Giả Kim', N'NXB Hội Nhà Văn', 79.00, N'Cậu bé chăn cừu Santiago băng qua sa mạc đi tìm kho báu và tìm thấy chính mình.', '1988-01-01', 'book_31.webp', 90),
    (32, 978004384, N'Veronika Quyết Chết', N'NXB Văn Học', 88.00, N'Veronika tỉnh dậy trong bệnh viện tâm thần và biết mình chỉ còn vài ngày để sống.', '1998-08-11', 'book_32.webp', 43),
    (33, 978004521, N'Mười Một Phút', N'NXB Văn Học', 92.00, N'Maria rời làng quê Brazil đến Geneva và học lại ý nghĩa của tình yêu.', '2003-05-02', 'book_33.webp', 37),
    (34, 978004658, N'Brida', N'NXB Văn Học', 85.00, N'Cô gái Ireland đi tìm con đường của phù thủy và của tình yêu định mệnh.', '1990-03-19', 'book_34.webp', 32),
    (35, 978004795, N'Zahir', N'NXB Văn Học', 105.00, N'Một nhà văn đi tìm người vợ mất tích, và tìm luôn phần đời mình đã đánh rơi.', '2005-04-08', 'book_35.webp', 26),
    (36, 978004932, N'Nhật Ký Một Pháp Sư', N'NXB Văn Học', 97.00, N'Hành trình 700 km trên con đường hành hương Santiago de Compostela.', '1987-06-01', 'book_36.svg', 21),
    (37, 978005069, N'Bên Bờ Sông Piedra Tôi Ngồi Khóc', N'NXB Văn Học', 83.00, N'Pilar gặp lại người bạn thời thơ ấu, nay đã thành một người chữa lành.', '1994-11-15', 'book_37.webp', 35),
    (38, 978005206, N'Nữ Chiến Binh Ánh Sáng', N'NXB Văn Học', 76.00, N'Tập những lời chỉ dẫn ngắn dành cho người dám sống theo giấc mơ của mình.', '1997-02-20', 'book_38.svg', 40),
    (39, 978005343, N'Ngoại Tình', N'NXB Văn Học', 99.00, N'Linda có tất cả nhưng vẫn thấy trống rỗng, và bước qua một ranh giới.', '2014-08-19', 'book_39.webp', 23),
    (40, 978005480, N'Aleph', N'NXB Văn Học', 115.00, N'Chuyến tàu xuyên Siberia trở thành hành trình đối diện với lỗi lầm kiếp trước.', '2010-08-16', 'book_40.webp', 27),
    (41, 978005617, N'Cánh Đồng Bất Tận', N'NXB Trẻ', 68.00, N'Hai chị em theo cha lênh đênh chăn vịt khắp đồng bằng, mang theo vết thương của người lớn.', '2005-11-01', 'book_41.webp', 58),
    (42, 978005754, N'Gió Lẻ Và 9 Câu Chuyện Khác', N'NXB Trẻ', 72.00, N'Tập truyện ngắn về những con người lạc nhau giữa đời sống miền Tây.', '2008-09-15', 'book_42.webp', 41),
    (43, 978005891, N'Sông', N'NXB Trẻ', 89.00, N'Ân ngược dòng sông Di để viết một cuốn du khảo và để trốn chính mình.', '2012-09-20', 'book_43.webp', 36),
    (44, 978006028, N'Đảo', N'NXB Trẻ', 78.00, N'Tập truyện ngắn về những hòn đảo trong lòng người, nơi ai cũng cô đơn theo cách riêng.', '2014-04-10', 'book_44.webp', 33),
    (45, 978006165, N'Không Ai Qua Sông', N'NXB Trẻ', 82.00, N'Mười một truyện ngắn day dứt về những cuộc chia lìa không lời từ biệt.', '2016-10-05', 'book_45.webp', 30),
    (46, 978006302, N'Ngọn Đèn Không Tắt', N'NXB Trẻ', 59.00, N'Tập truyện đầu tay đem về giải thưởng Văn học tuổi 20 cho tác giả.', '2000-06-01', 'book_46.webp', 44),
    (47, 978006439, N'Giao Thừa', N'NXB Trẻ', 63.00, N'Những cái Tết của người nghèo miền Tây, ấm áp mà cũng nhiều ngậm ngùi.', '2003-01-20', 'book_47.webp', 38),
    (48, 978006576, N'Khói Trời Lộng Lẫy', N'NXB Thời Đại', 86.00, N'Người chị bắt cóc em trai để giữ lại một phần tuổi thơ đã mất.', '2010-12-12', 'book_48.webp', 29),
    (49, 978006713, N'Biển Của Mỗi Người', N'NXB Trẻ', 74.00, N'Tản văn về biển, về quê, về những chuyến đi không cần điểm đến.', '2008-05-18', 'book_49.webp', 31),
    (50, 978006850, N'Hành Lý Hư Vô', N'NXB Trẻ', 91.00, N'Tập tản văn nhẹ như khói về việc mang theo gì và bỏ lại gì trong đời.', '2019-03-08', 'book_50.webp', 34);
SET IDENTITY_INSERT books OFF;
GO

/* ------------------ LIÊN KẾT SÁCH <-> TÁC GIẢ ------------------ */
INSERT INTO book_author (bookid, author_id) VALUES
    (1, 1),
    (2, 1),
    (3, 1),
    (4, 1),
    (5, 1),
    (6, 1),
    (7, 1),
    (8, 1),
    (9, 1),
    (10, 1),
    (11, 2),
    (12, 2),
    (13, 2),
    (14, 2),
    (15, 2),
    (16, 2),
    (17, 2),
    (18, 2),
    (19, 2),
    (20, 2),
    (21, 3),
    (22, 3),
    (23, 3),
    (24, 3),
    (25, 3),
    (26, 3),
    (27, 3),
    (28, 3),
    (29, 3),
    (30, 3),
    (31, 4),
    (32, 4),
    (33, 4),
    (34, 4),
    (35, 4),
    (36, 4),
    (37, 4),
    (38, 4),
    (39, 4),
    (40, 4),
    (41, 5),
    (42, 5),
    (43, 5),
    (44, 5),
    (45, 5),
    (46, 5),
    (47, 5),
    (48, 5),
    (49, 5),
    (50, 5),
    (4, 2),
    (29, 5),
    (46, 1);
GO

/* -------------------------- NGƯỜI DÙNG ------------------------- */
SET IDENTITY_INSERT users ON;
INSERT INTO users (id, email, fullname, phone, passwd, signup_date, last_login, is_admin) VALUES
    (1, 'admin@bookstore.local', N'Trần Minh Thọ', 912345678, 'e10adc3949ba59abbe56e057f20f883e', GETDATE(), NULL, 1),
    (2, 'user@bookstore.local', N'Nguyễn Văn An', 987654321, 'e10adc3949ba59abbe56e057f20f883e', GETDATE(), NULL, 0),
    (3, 'mai@bookstore.local', N'Lê Thị Mai', 934567890, 'e10adc3949ba59abbe56e057f20f883e', GETDATE(), NULL, 0),
    (4, 'hung@bookstore.local', N'Phạm Quốc Hùng', 901234567, 'e10adc3949ba59abbe56e057f20f883e', GETDATE(), NULL, 0);
SET IDENTITY_INSERT users OFF;
GO

/* ---------------------------- REVIEW --------------------------- */
INSERT INTO rating (userid, bookid, rating, review_text) VALUES
    (2, 1, 5, N'Đọc xong cứ thấy tiếc cho Ngạn. Văn nhẹ mà thấm, đọc một mạch hết luôn.'),
    (3, 1, 4, N'Mình thích làng Đo Đo trong truyện, tả cảnh rất có không khí.'),
    (4, 1, 5, N'Cuốn này đọc lần hai vẫn hay, đáng tiền.'),
    (2, 3, 5, N'Tuổi thơ miền quê hiện lên rõ mồn một, đọc mà nhớ nhà.'),
    (3, 3, 4, N'Sách in đẹp, giấy tốt, nội dung thì khỏi bàn.'),
    (2, 11, 5, N'Dế Mèn thì ai cũng biết rồi, nhưng đọc bản in mới vẫn thích.'),
    (4, 11, 5, N'Mua cho em trai, bé đọc hết trong hai ngày.'),
    (3, 21, 4, N'Rừng Na Uy buồn nhưng đẹp. Không dành cho lúc đang tâm trạng.'),
    (2, 21, 5, N'Bản dịch mượt, đọc trôi.'),
    (4, 31, 5, N'Nhà Giả Kim ngắn mà nhiều câu đáng chép lại.'),
    (3, 31, 3, N'Hay nhưng hơi bị tung hô quá mức so với kỳ vọng của mình.'),
    (2, 41, 5, N'Cánh Đồng Bất Tận đọc xong nặng lòng mấy hôm.'),
    (4, 41, 4, N'Giọng văn miền Tây rất riêng, không lẫn đi đâu được.'),
    (3, 12, 4, N'Vợ Chồng A Phủ đọc lại sau nhiều năm thấy khác hẳn hồi cấp ba.'),
    (2, 22, 4, N'Kafka Bên Bờ Biển kỳ ảo, đọc chậm mới ngấm.'),
    (4, 32, 4, N'Veronika Quyết Chết khiến mình nghĩ nhiều về việc sống cho mình.'),
    (3, 42, 4, N'Tập truyện ngắn nào của chị Tư cũng buồn mà đẹp.'),
    (2, 5, 4, N'Cô Gái Đến Từ Hôm Qua - đọc để cười rồi lại thấy thương.'),
    (4, 15, 3, N'Nhà Nghèo hơi nặng nề, đọc cần tâm trạng.'),
    (3, 25, 5, N'Người Tình Sputnik là cuốn Murakami mình thích nhất.');
GO

PRINT 'Da them du lieu mau: 5 tac gia, 50 sach, 4 tai khoan, 20 review.';
GO
