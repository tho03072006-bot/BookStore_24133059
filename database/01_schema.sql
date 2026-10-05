/* =====================================================================
   ĐỀ SỐ 02 - CÂU 1: Tạo cấu trúc database đúng như sơ đồ trong đề thi.
   Sinh viên: Trần Minh Thọ - MSSV: 24133059

   Chạy file này TRƯỚC, sau đó chạy 02_seed.sql để có dữ liệu test.
   Hệ quản trị: Microsoft SQL Server (instance đang dùng: localhost,1433).

   GHI CHÚ VỀ COLLATE:
   Database được tạo với collation Vietnamese_100_CI_AS_SC_UTF8 để cột
   VARCHAR lưu được tiếng Việt CÓ DẤU đúng như đề yêu cầu (đề dùng
   varchar cho title / author_name / publisher, không phải nvarchar).
   Đây là collation UTF-8 có từ SQL Server 2019 trở lên.

   GHI CHÚ VỀ KIỂU TEXT:
   Đề vẽ description và review_text kiểu "text". SQL Server KHÔNG cho phép
   kiểu LOB cũ (text/ntext) nằm trong database dùng collation UTF-8
   (lỗi 4188 - "The legacy LOB types do not support UTF-8"). Kiểu text
   cũng đã bị Microsoft khai tử. Vì vậy hai cột này dùng VARCHAR(MAX) -
   chính là kiểu thay thế mà Microsoft khuyến nghị cho text, giữ nguyên ý
   nghĩa "chuỗi dài không giới hạn" của đề.
   ===================================================================== */

USE master;
GO

IF DB_ID('BookStore') IS NOT NULL
BEGIN
    ALTER DATABASE BookStore SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE BookStore;
END
GO

CREATE DATABASE BookStore COLLATE Vietnamese_100_CI_AS_SC_UTF8;
GO

USE BookStore;
GO

/* ---------------------------------------------------------------------
   Bảng author - tác giả. Tất cả ID đều tăng tự động (IDENTITY).
   --------------------------------------------------------------------- */
CREATE TABLE author (
    author_id     INT IDENTITY(1,1) NOT NULL,
    author_name   VARCHAR(100)          NULL,
    date_of_birth DATE                  NULL,
    CONSTRAINT PK_author PRIMARY KEY (author_id)
);
GO

/* ---------------------------------------------------------------------
   Bảng books - sách.
   --------------------------------------------------------------------- */
CREATE TABLE books (
    bookid       INT IDENTITY(1,1) NOT NULL,
    isbn         INT                   NULL,
    title        VARCHAR(200)          NULL,
    publisher    VARCHAR(100)          NULL,
    price        DECIMAL(6,2)          NULL,
    description  VARCHAR(MAX)          NULL,   -- đề ghi "text"
    publish_date DATE                  NULL,
    cover_image  VARCHAR(100)          NULL,
    quantity     INT                   NULL,
    CONSTRAINT PK_books PRIMARY KEY (bookid)
);
GO

/* ---------------------------------------------------------------------
   Bảng book_author - quan hệ nhiều-nhiều giữa books và author.
   Khoá chính là cặp (bookid, author_id).
   --------------------------------------------------------------------- */
CREATE TABLE book_author (
    bookid    INT NOT NULL,
    author_id INT NOT NULL,
    CONSTRAINT PK_book_author PRIMARY KEY (bookid, author_id),
    CONSTRAINT FK_book_author_books  FOREIGN KEY (bookid)
        REFERENCES books(bookid)   ON DELETE CASCADE,
    CONSTRAINT FK_book_author_author FOREIGN KEY (author_id)
        REFERENCES author(author_id) ON DELETE CASCADE
);
GO

/* ---------------------------------------------------------------------
   Bảng users - người dùng. is_admin = 1 là quản trị viên.
   passwd varchar(32) vừa đúng độ dài chuỗi băm MD5 dạng hex (32 ký tự).
   --------------------------------------------------------------------- */
CREATE TABLE users (
    id          INT IDENTITY(1,1) NOT NULL,
    email       VARCHAR(50)       NOT NULL,
    fullname    NVARCHAR(50)          NULL,
    phone       INT                   NULL,
    passwd      VARCHAR(32)       NOT NULL,
    signup_date DATETIME              NULL,
    last_login  DATETIME              NULL,
    is_admin    BIT                   NULL,
    CONSTRAINT PK_users PRIMARY KEY (id)
);
GO

/* ---------------------------------------------------------------------
   Bảng rating - đánh giá / review của user cho từng cuốn sách.
   Khoá chính là cặp (userid, bookid): mỗi user chỉ review 1 lần / 1 sách.
   --------------------------------------------------------------------- */
CREATE TABLE rating (
    userid      INT     NOT NULL,
    bookid      INT     NOT NULL,
    rating      TINYINT     NULL,
    review_text VARCHAR(MAX) NULL,   -- đề ghi "text"
    CONSTRAINT PK_rating PRIMARY KEY (userid, bookid),
    CONSTRAINT FK_rating_users FOREIGN KEY (userid)
        REFERENCES users(id)     ON DELETE CASCADE,
    CONSTRAINT FK_rating_books FOREIGN KEY (bookid)
        REFERENCES books(bookid) ON DELETE CASCADE
);
GO

PRINT 'Da tao xong database BookStore va 5 bang: author, books, book_author, users, rating.';
GO
