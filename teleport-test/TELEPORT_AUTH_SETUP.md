# Teleport Authentication Setup

## Tổng quan

Hệ thống Warehouse Manager hiện đã được cấu hình để **chỉ sử dụng Teleport authentication** (Zero Trust Architecture). Tất cả user phải đăng nhập qua Teleport để truy cập ứng dụng.

## Danh sách User đã được tạo trong Teleport

Các user sau đã được tạo trong Teleport:

1. **admin** (ADMIN) - ✓ Đã tồn tại
2. **jane_updated** (USER) - ✓ Đã tạo
3. **jane_doe** (USER) - ✓ Đã tạo
4. **jane_doe_123** (USER) - ✓ Đã tạo
5. **admin2** (ADMIN) - ✓ Đã tạo

## Cách User đăng nhập

### Bước 1: Hoàn tất đăng ký Teleport (lần đầu)

Mỗi user mới cần hoàn tất đăng ký bằng cách:

1. Truy cập invitation link được tạo khi tạo user:
   - `jane_updated`: https://localhost:3080/web/invite/8953d5cc37e736912ffd59701c0b1149
   - `jane_doe`: https://localhost:3080/web/invite/d584c11046e8ce61649fed943dc4282a
   - `jane_doe_123`: https://localhost:3080/web/invite/fab26faee26af6fc5508d69b265d2b7e
   - `admin2`: https://localhost:3080/web/invite/07719666190927b1c6b102ca2c13b16d

2. Đặt mật khẩu cho tài khoản Teleport
3. Sau đó có thể đăng nhập vào Teleport Web UI

### Bước 2: Đăng nhập vào ứng dụng

1. Truy cập Teleport Web UI: https://localhost:3080
2. Đăng nhập với username và password Teleport
3. Chọn ứng dụng **warehouse-frontend** từ danh sách
4. Hệ thống sẽ tự động authenticate và chuyển đến ứng dụng

## Tạo User mới

Khi tạo user mới trong ứng dụng, bạn cần:

1. Tạo user trong database (qua UI hoặc API)
2. Tạo user tương ứng trong Teleport:

```bash
cd /home/kali/MMANM/teleport-test
tctl --config=teleport.yaml users add <username> --roles=access
```

Hoặc chạy script tự động:

```bash
cd /home/kali/MMANM/teleport-test
./create_teleport_users.sh
```

## Reset Password Teleport

Nếu user quên mật khẩu Teleport:

```bash
cd /home/kali/MMANM/teleport-test
tctl --config=teleport.yaml users reset <username>
```

## Cấu hình đã thay đổi

### Frontend
- Form login bị ẩn khi truy cập qua Teleport (`warehouse-frontend.localhost`)
- Chỉ hiển thị thông báo yêu cầu đăng nhập qua Teleport

### Backend
- Endpoint `/api/auth/login` sẽ từ chối nếu request đến từ Teleport
- Tất cả authentication phải đi qua Teleport identity filter

## Lưu ý

- **Không thể đăng nhập trực tiếp** qua form login khi truy cập qua Teleport
- Tất cả user phải có tài khoản Teleport tương ứng
- Username trong database phải khớp với username trong Teleport
- Role trong database sẽ được sử dụng cho authorization, không phải role Teleport

## Troubleshooting

### User không thể đăng nhập

1. Kiểm tra user đã tồn tại trong Teleport:
   ```bash
   tctl --config=teleport-test/teleport.yaml users ls
   ```

2. Kiểm tra user đã hoàn tất đăng ký (đặt password) chưa

3. Kiểm tra user có quyền truy cập app:
   ```bash
   tctl --config=teleport-test/teleport.yaml users get <username>
   ```

### Lỗi CORS

- Đảm bảo frontend gọi API qua cùng origin (không gọi trực tiếp đến backend)
- Kiểm tra `apiConfig.ts` đã được cập nhật để dùng `window.location.origin`

