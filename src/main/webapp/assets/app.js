/* Hỏi lại trước các thao tác không hoàn tác được.
   Dùng một listener chung ở document nên các dòng sinh thêm sau vẫn chạy. */
const XAC_NHAN = {
    "[data-confirm-delete]": "Bạn có chắc muốn xóa cuốn sách này? Thao tác này không hoàn tác được.",
    "[data-confirm-remove]": "Bỏ cuốn sách này khỏi giỏ hàng?",
    "[data-confirm-clear]": "Xóa toàn bộ giỏ hàng?",
    "[data-confirm-cancel]": "Hủy đơn hàng này? Sách sẽ được trả lại kho."
};

document.addEventListener("submit", (event) => {
    for (const [selector, message] of Object.entries(XAC_NHAN)) {
        if (event.target.matches(selector) && !window.confirm(message)) {
            event.preventDefault();
            return;
        }
    }
});
