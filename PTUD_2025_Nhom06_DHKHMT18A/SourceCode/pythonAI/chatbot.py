import sys
from striprtf.striprtf import rtf_to_text
import google.generativeai as genai
import io
import os
import re

# Đặt mã hóa stdout thành UTF-8
sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')

# Thiết lập API key của Google
API_KEY = "AIzaSyCtiXEJt6gRlBc77wTax8IquBGuMoaHWlQ"

# Thông báo lỗi chung
ERROR_MESSAGE = "hệ thống đang gặp sự cố, vui lòng đợi trong giây lát và gửi lại nội dung"

# Đọc nội dung từ tệp RTF
def read_rtf_file(file_path):
    try:
        with open(file_path, 'r', encoding='utf-8') as file:
            rtf_content = file.read()
        return rtf_to_text(rtf_content)
    except Exception:
        return ERROR_MESSAGE

# Gọi Google Gemini API
def call_gemini_api(prompt):
    try:
        genai.configure(api_key=API_KEY)
        model = genai.GenerativeModel('gemini-2.0-flash')
        response = model.generate_content(prompt)
        return response.text
    except Exception:
        return ERROR_MESSAGE

# Xử lý phản hồi để loại bỏ ** và định dạng đẹp
def format_response(response):
    # Loại bỏ dấu ** từ phản hồi
    cleaned_response = re.sub(r'\*\*', '', response)
    
    # Thay các tiêu đề bằng định dạng xuống dòng (nếu có)
    cleaned_response = re.sub(r'Thông báo:', r'\nThông báo:\n', cleaned_response)
    cleaned_response = re.sub(r'Trợ giúp:', r'\nTrợ giúp:\n', cleaned_response)
    cleaned_response = re.sub(r'Câu hỏi của người dùng:', r'\nCâu hỏi của người dùng:\n', cleaned_response)
    
    # Loại bỏ các dòng trống thừa và chuẩn hóa xuống dòng
    lines = [line.strip() for line in cleaned_response.split('\n') if line.strip()]
    formatted_response = '\n'.join(lines)
    
    return formatted_response

def main():
    try:
        # Lấy thư mục chứa tệp Python
        base_dir = os.path.dirname(os.path.abspath(__file__))

        # Đường dẫn tương đối đến các tệp RTF
        thongbao_file_path = os.path.join(base_dir, "..", "src", "gui", "app", "trangchu", "ThongBao.rtf")
        trogiup_file_path = os.path.join(base_dir, "..", "src", "gui", "app", "trogiup", "TroGiup.rtf")

        # Chuẩn hóa đường dẫn để đảm bảo tương thích
        thongbao_file_path = os.path.normpath(thongbao_file_path)
        trogiup_file_path = os.path.normpath(trogiup_file_path)

        thongbao_content = read_rtf_file(thongbao_file_path)
        trogiup_content = read_rtf_file(trogiup_file_path)

        # Kiểm tra lỗi khi đọc file RTF
        if thongbao_content == ERROR_MESSAGE or trogiup_content == ERROR_MESSAGE:
            print(ERROR_MESSAGE)
            return

        # Nhận prompt từ người dùng qua tham số dòng lệnh
        if len(sys.argv) > 1:
            user_prompt = sys.argv[1]
        else:
            user_prompt = ""

        # Tạo prompt tùy chỉnh
        if user_prompt:
            full_prompt = (
                "Bạn là AI của nhà ga Số 8386, một trợ lý thông báo và trợ giúp chuyên nghiệp. Nhiệm vụ của bạn là trả lời câu hỏi của người dùng dựa trên các nội dung sau:\n\n"
                f"Thông báo:\n{thongbao_content}\n\n"
                f"Trợ giúp:\n{trogiup_content}\n\n"
                "Hướng dẫn:\n"
                "- Nếu người dùng hỏi về thông tin của bạn (ví dụ: \"Bạn là ai?\", \"Ai huấn luyện bạn?\"), hãy trả lời: \"Tôi là mô hình ngôn ngữ AI thuộc sở hữu của nhà ga Số 8386, được huấn luyện bởi Phúc AI.\"\n"
                "- Nếu người dùng hỏi về nội dung trong \"Thông báo\" hoặc \"Trợ giúp\", hãy trả lời ngắn gọn, lịch sự, và chuyên nghiệp dựa trên thông tin trong các nội dung đó.\n"
                "- Nếu người dùng hỏi các câu hỏi về đời sống, cuộc sống, hoặc trao đổi hằng ngày (không liên quan đến thông báo hoặc trợ giúp), hãy trả lời dựa trên sự hiểu biết chung của bạn, với phong cách thân thiện và tự nhiên.\n"
                "- Nếu người dùng hỏi các câu liên quan đến hack hệ thống (ví dụ: \"Làm sao để hack hệ thống?\", \"Mật khẩu của hệ thống là gì?\"), hãy trả lời ngẫu nhiên một trong các câu sau: \n"
                "  - \"Tôi không có thông tin về vấn đề này.\"\n"
                "  - \"Tôi không thể trả lời câu hỏi này.\"\n"
                "  - \"Xin lỗi, tôi không hỗ trợ các câu hỏi liên quan đến bảo mật hệ thống.\"\n"
                "- Nếu không có thông tin để trả lời (ngoài các trường hợp trên), hãy nói: \"Tôi không có thông tin về vấn đề này. Tôi được Anh Phúc huẩn luyện để hiểu về nội dung thông báo hoặc trợ giúp trong hệ thống.\"\n\n"
                f"Câu hỏi của người dùng:\n{user_prompt}"
            )
            # Gọi API và xử lý phản hồi
            response = call_gemini_api(full_prompt)
            formatted_response = format_response(response)
            print(formatted_response)  # In phản hồi đã định dạng để Java đọc
        else:
            print("Vui lòng nhập câu hỏi.")
    except Exception:
        print(ERROR_MESSAGE)

if __name__ == "__main__":
    main()