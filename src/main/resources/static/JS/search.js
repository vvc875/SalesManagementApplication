document.addEventListener("DOMContentLoaded", function () {
    const searchButton = document.getElementById("searchButton");
    const categoryInput = document.getElementById("categoryName");
    const resultContainer = document.getElementById("result");

    searchButton.addEventListener("click", function () {
        const category = categoryInput.value.trim();

        if (category === "") {
            resultContainer.innerHTML = "<p>⚠️ Vui lòng nhập tên danh mục!</p>";
            return;
        }

        fetch(`http://localhost:8080/api/product/category?name=${encodeURIComponent(category)}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error("Lỗi khi gọi API");
                }
                return response.json();
            })
            .then(data => {
                if (data.length === 0) {
                    resultContainer.innerHTML = "<p>❌ Không tìm thấy sản phẩm nào.</p>";
                    return;
                }

                // Hiển thị danh sách sản phẩm
                let html = "<h3>Kết quả tìm kiếm:</h3><ul>";
                data.forEach(product => {
                    html += `
                        <li>
                            <strong>${product.name}</strong><br>
                            Giá: ${product.price.toLocaleString()}₫<br>
                            Số lượng: ${product.quantity}<br>
                            Mô tả: ${product.description || "Không có mô tả"}<br>
                            <hr>
                        </li>`;
                });
                html += "</ul>";
                resultContainer.innerHTML = html;
            })
            .catch(error => {
                console.error("Lỗi:", error);
                resultContainer.innerHTML = "<p style='color:red;'>Đã xảy ra lỗi khi tìm kiếm!</p>";
            });
    });
});
