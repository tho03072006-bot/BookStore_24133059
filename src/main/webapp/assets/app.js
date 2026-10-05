/* Hỏi lại trước các thao tác không hoàn tác được.
   Dùng một listener chung ở document nên các dòng sinh thêm sau vẫn chạy. */
const XAC_NHAN = {
    "[data-confirm-delete]": "Bạn có chắc muốn xóa cuốn sách này? Thao tác này không hoàn tác được.",
    "[data-confirm-remove]": "Bỏ cuốn sách này khỏi giỏ hàng?",
    "[data-confirm-clear]": "Xóa toàn bộ giỏ hàng?",
    "[data-confirm-cancel]": "Hủy đơn hàng này? Sách sẽ được trả lại kho."
};

function pageKey(url) {
    const parsed = new URL(url, location.origin);
    parsed.searchParams.sort();
    return parsed.pathname + "?" + parsed.searchParams.toString();
}

document.addEventListener("submit", (event) => {
    for (const [selector, message] of Object.entries(XAC_NHAN)) {
        if (event.target.matches(selector) && !window.confirm(message)) {
            event.preventDefault();
            return;
        }
    }
    const form = event.target;
    if (form.matches("[data-pending-label]")) {
        const button = event.submitter || form.querySelector('button[type="submit"]');
        if (button) {
            button.disabled = true;
            button.textContent = form.dataset.pendingLabel;
            form.setAttribute("aria-busy", "true");
        }
    }
    if (form.matches(".add-to-cart")) {
        try {
            const back = form.elements.returnUrl?.value;
            const prefix = new URL(form.action).pathname.replace(/\/cart\/add$/, "");
            sessionStorage.setItem("cartScroll", JSON.stringify({path: pageKey(back ? prefix + back : location.href), y: scrollY}));
        }
        catch (_) { /* Chặn storage không ảnh hưởng việc gửi form. */ }
    }
});

document.querySelector("[data-error-summary]")?.focus();
try {
    const saved = JSON.parse(sessionStorage.getItem("cartScroll"));
    sessionStorage.removeItem("cartScroll");
    if (saved?.path === pageKey(location.href)) window.scrollTo(0, saved.y);
} catch (_) { /* Tính năng bổ trợ. */ }

document.querySelectorAll('input[type="password"]').forEach((input) => {
    const button = document.createElement("button");
    button.type = "button";
    button.className = "password-toggle btn small secondary";
    button.textContent = "Hiện mật khẩu";
    button.setAttribute("aria-controls", input.id);
    button.setAttribute("aria-pressed", "false");
    button.addEventListener("click", () => {
        const show = input.type === "password";
        input.type = show ? "text" : "password";
        button.textContent = show ? "Ẩn mật khẩu" : "Hiện mật khẩu";
        button.setAttribute("aria-pressed", String(show));
    });
    input.after(button);
});
