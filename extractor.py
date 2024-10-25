import re
import copy
import javalang
from CONSTANT import *


def uncrack_the_code(code):
    text = code.replace("public class Subject {", "")
    text = text[::-1].replace("}", "", 1)[::-1]
    return text


def read_java_file(file_path):
    with open(file_path, "r", encoding="utf-8") as file:
        return file.read()


class JavaExtractor:
    def __init__(self, java_file_path=None):
        self.java_file_path = java_file_path
        self.class_dict = self.main(java_file_path)

    # 找到类的定义
    def extract_class_definitions(self, java_code):
        class_definitions = {}
        class_pattern = re.compile(r"(public|protected|private)?\s*class\s+(\w+)\s*\{")

        lines = java_code.splitlines()
        current_class = None
        class_start_line = None
        brace_count = 0

        for i, line in enumerate(lines):
            if current_class is None:
                match = class_pattern.search(line)
                if match:
                    current_class = match.group(2)
                    class_start_line = i
                    brace_count = 0
                    brace_count += line.count("{") - line.count("}")
            else:
                brace_count += line.count("{") - line.count("}")

                if brace_count == 0:
                    class_def = "\n".join(lines[class_start_line : i + 1])
                    class_definitions[current_class] = class_def
                    current_class = None

        return class_definitions

    def extract_methods_from_class(self, class_code):
        # 首先去除注释
        class_code = re.sub(r"/\*.*?\*/", "", class_code, flags=re.DOTALL)

        # 去除多余的空行和空白字符
        class_code = re.sub(r"\s*\n", "\n", class_code)

        method_definitions = {}
        method_signature_pattern = re.compile(
            r"(?P<modifiers>(?:public|protected|private|static|final|abstract|synchronized)\s+)*"
            r"(?P<return_type>[\w<>\[\],\s]+)\s+"
            r"(?P<method_name>\w+)\s*"
            r"\((?P<params>.*?)\)\s*"
            r"\{",
            re.DOTALL,
        )

        pos = 0
        while True:
            match = method_signature_pattern.search(class_code, pos)
            if not match:
                break
            method_name = match.group("method_name")
            method_start = match.start()
            brace_count = 1
            pos = match.end()

            while pos < len(class_code) and brace_count > 0:
                if class_code[pos] == "{":
                    brace_count += 1
                elif class_code[pos] == "}":
                    brace_count -= 1
                pos += 1

            method_body = class_code[method_start:pos].strip()
            modifiers = match.group("modifiers")
            if modifiers:
                modifiers = modifiers.strip()
            else:
                if method_body.startswith("public "):
                    modifiers = "public"
                elif method_body.startswith("protected "):
                    modifiers = "protected"
                elif method_body.startswith("private "):
                    modifiers = "private"
                else:
                    modifiers = "private"
            if method_name not in method_definitions:
                method_definitions[method_name] = {
                    "visibility": modifiers,
                    "body": method_body,
                    "called_by": [],
                }
            else:
                method_definitions[method_name]["body"] += "\n\t\t" + method_body

        method_names = method_definitions.keys()
        # 我还想知道所有方法被哪些方法给调用了
        for method_name, method_definition in method_definitions.items():
            for other_method_name in method_names:
                if other_method_name != method_name:
                    if other_method_name in method_definition["body"]:
                        method_definitions[other_method_name]["called_by"].append(
                            method_name
                        )
        return method_definitions

    # 主函数
    def main(self, java_file_path):
        # 读取Java文件内容
        java_code = read_java_file(java_file_path)
        java_code = uncrack_the_code(java_code)
        # 查找类定义
        class_definitions = self.extract_class_definitions(java_code)

        result_dict = {}
        # 遍历每个类，找到每个类中的函数
        for class_name, class_definition in class_definitions.items():
            # print(f"Class Name: {class_name}")

            method_definitions = self.extract_methods_from_class(class_definition)
            result_dict[class_name] = method_definitions
        return result_dict


class TestCaseExtractor:
    def __init__(self) -> None:
        pass

    def extract_test_cases(self, response) -> tuple:
        if response.find("```") == -1:
            code = response
        else:
            code = re.findall(r"```java(.*?)```", response, re.DOTALL)[0]
        # print(code)
        tree = javalang.parse.parse(code)
        imports = []
        test_cases = []
        for imp in tree.imports:
            import_statement = "import "
            if imp.static:
                import_statement += "static "
            import_statement += imp.path
            if imp.wildcard:
                import_statement += ".*"
            import_statement += ";"
            # print(import_statement)
            imports.append(import_statement)

        for _, node in tree:
            if isinstance(node, javalang.tree.MethodDeclaration):
                # 检查是否有 @Test 注解
                annotations = [annotation.name for annotation in node.annotations]
                if "Test" in annotations:
                    method_name = node.name
                    # 获取方法的起始位置
                    start_pos = node.position.line - 1

                    # 修改后的 get_end_line 函数
                    def get_end_line(node):
                        end_line = start_pos
                        if isinstance(node, javalang.ast.Node):
                            if hasattr(node, "position") and node.position:
                                end_line = node.position.line
                            for child in node.children:
                                if isinstance(child, list):
                                    for item in child:
                                        child_end_line = get_end_line(item)
                                        end_line = max(end_line, child_end_line)
                                elif isinstance(child, javalang.ast.Node):
                                    child_end_line = get_end_line(child)
                                    end_line = max(end_line, child_end_line)
                                else:
                                    # 非 AST 节点，跳过
                                    pass
                        return end_line

                    # 初始结束行，获取方法体内最后一个节点的行号
                    end_pos = get_end_line(node)

                    # 从 end_pos 开始，查找方法结束的 '}'
                    code_lines = code.splitlines()
                    brace_count = 0
                    for idx in range(start_pos, len(code_lines)):
                        line = code_lines[idx]
                        brace_count += line.count("{") - line.count("}")
                        if brace_count == 0:
                            end_pos = idx + 1  # 包含当前行
                            break

                    method_body = "\n".join(code_lines[start_pos:end_pos])
                    # print(f"Method Name: {method_name}")
                    # print(f"Method Body:\n{method_body}\n")
                    test_cases.append({"name": method_name, "body": method_body})

        return imports, test_cases


if __name__ == "__main__":
    # test_case_extractor = TestCaseExtractor()
    # with open("test.txt", "r", encoding="utf-8") as file:
    #     response = file.read()
    # # print(response)
    # print(test_case_extractor.extract_test_cases(response))
    print((JavaExtractor(JAVA_FILE_PATH).class_dict["ParameterTool"].values()))
