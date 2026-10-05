<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%--
    THANH TOÃN COD - nháº­p thÃ´ng tin nháº­n hÃ ng rá»“i chá»‘t Ä‘Æ¡n.
    Tráº§n Minh Thá» - 24133059
--%>
<html>
<head>
    <title>Thanh toÃ¡n COD</title>
</head>
<body>

<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<div class="page-title">
    <h1>Thanh toÃ¡n</h1>
    <a class="btn secondary" href="${ctx}/cart">&laquo; Quay láº¡i giá» hÃ ng</a>
</div>

<c:if test="${not empty error}">
    <div class="alert error" role="alert"><c:out value="${error}"/></div>
</c:if>

<div class="checkout-grid">

    <%-- ----------------------- ThÃ´ng tin nháº­n hÃ ng ----------------------- --%>
    <div class="card">
        <h2>ThÃ´ng tin nháº­n hÃ ng</h2>

        <form method="post" action="${ctx}/checkout">

            <div class="form-row">
                <label for="receiverName">NgÆ°á»i nháº­n <span class="muted">(báº¯t buá»™c)</span></label>
                <input type="text" id="receiverName" name="receiverName" required maxlength="50"
                       value="<c:out value='${receiverName}'/>" placeholder="Nguyá»…n VÄƒn A">
            </div>

            <div class="form-row">
                <label for="receiverPhone">Sá»‘ Ä‘iá»‡n thoáº¡i <span class="muted">(báº¯t buá»™c)</span></label>
                <input type="tel" id="receiverPhone" name="receiverPhone" required
                       pattern="0[0-9]{9,10}" maxlength="11"
                       value="<c:out value='${receiverPhone}'/>" placeholder="0912345678">
                <div class="hint">10 hoáº·c 11 chá»¯ sá»‘, báº¯t Ä‘áº§u báº±ng sá»‘ 0.</div>
            </div>

            <div class="form-row">
                <label for="address">Äá»‹a chá»‰ nháº­n hÃ ng <span class="muted">(báº¯t buá»™c)</span></label>
                <input type="text" id="address" name="address" required maxlength="200"
                       value="<c:out value='${address}'/>"
                       placeholder="Sá»‘ nhÃ , Ä‘Æ°á»ng, phÆ°á»ng/xÃ£, quáº­n/huyá»‡n, tá»‰nh/thÃ nh">
            </div>

            <div class="form-row">
                <label for="note">Ghi chÃº cho ngÆ°á»i giao hÃ ng</label>
                <textarea id="note" name="note" maxlength="200"
                          placeholder="VÃ­ dá»¥: giao giá» hÃ nh chÃ­nh"><c:out value="${note}"/></textarea>
            </div>

            <div class="form-row">
                <label>HÃ¬nh thá»©c thanh toÃ¡n</label>
                <div class="payment-box">
                    <input type="radio" id="cod" name="paymentMethod" value="COD" checked>
                    <label for="cod">
                        <strong>COD &ndash; Thanh toÃ¡n khi nháº­n hÃ ng</strong>
                        <span class="hint">Báº¡n tráº£ tiá»n máº·t trá»±c tiáº¿p cho ngÆ°á»i giao hÃ ng.
                            Cá»­a hÃ ng hiá»‡n chá»‰ há»— trá»£ hÃ¬nh thá»©c nÃ y.</span>
                    </label>
                </div>
            </div>

            <div class="actions">
                <button type="submit" class="btn">Äáº·t hÃ ng</button>
                <a class="btn secondary" href="${ctx}/cart">Huá»·</a>
            </div>
        </form>
    </div>

    <%-- --------------------------- TÃ³m táº¯t Ä‘Æ¡n --------------------------- --%>
    <div class="card">
        <h2>ÄÆ¡n hÃ ng cá»§a báº¡n</h2>

        <table class="data">
            <thead>
            <tr>
                <th scope="col">SÃ¡ch</th>
                <th scope="col">SL</th>
                <th scope="col">ThÃ nh tiá»n</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="item" items="${cart.items}">
                <tr>
                    <td><c:out value="${item.title}"/></td>
                    <td>${item.quantity}</td>
                    <td>${item.subtotalText}</td>
                </tr>
            </c:forEach>
            </tbody>
            <tfoot>
            <tr>
                <th scope="row" colspan="2">Tá»•ng cá»™ng</th>
                <th>${cart.totalAmountText}</th>
            </tr>
            </tfoot>
        </table>

        <p class="hint" style="margin-top:12px;">
            Äáº·t xong, Ä‘Æ¡n á»Ÿ tráº¡ng thÃ¡i <strong>ÄÆ¡n hÃ ng má»›i</strong>. Báº¡n theo dÃµi
            tiáº¿n trÃ¬nh á»Ÿ má»¥c <a href="${ctx}/orders">ÄÆ¡n hÃ ng cá»§a tÃ´i</a>.
        </p>
    </div>
</div>

</body>
</html>
