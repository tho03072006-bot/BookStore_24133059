package edu.hcmute.webpr.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Model ánh xạ bảng {@code users}. {@code isAdmin} phân biệt 02 vai trò
 * User / Admin theo yêu cầu Câu 1.
 *
 * Đề số 02 - Trần Minh Thọ - 24133059
 */
public class User_24133059 implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String email;
    private String fullname;
    private Integer phone;
    private String passwd;
    private LocalDateTime signupDate;
    private LocalDateTime lastLogin;
    private boolean admin;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public Integer getPhone() {
        return phone;
    }

    public void setPhone(Integer phone) {
        this.phone = phone;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public LocalDateTime getSignupDate() {
        return signupDate;
    }

    public void setSignupDate(LocalDateTime signupDate) {
        this.signupDate = signupDate;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    /**
     * Số điện thoại lưu kiểu int nên số 0 đứng đầu bị mất. Hiển thị thì thêm
     * lại cho đủ 10 chữ số.
     */
    public String getPhoneDisplay() {
        if (phone == null) {
            return "";
        }
        String s = String.valueOf(phone);
        return (s.length() == 9) ? "0" + s : s;
    }

    /** Tên hiển thị trên header: ưu tiên họ tên, không có thì lấy email. */
    public String getDisplayName() {
        return (fullname == null || fullname.isBlank()) ? email : fullname;
    }
}
