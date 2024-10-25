import random
from extractor import JavaExtractor, TestCaseExtractor
from xml_reader import XMLReader
from llms import OpenAIClient, DeepSeekClient
from CONSTANT import *


def add_to_file(content, file_path):
    with open(file_path, "a+", encoding="utf-8") as f:
        f.write(content)


def write_to_file(content, file_path):
    with open(file_path, "w", encoding="utf-8") as f:
        f.write(content)


class TestCaseGenerator:
    def __init__(self) -> None:
        self.test_cases: list[dict] = []
        self.java_extractor = JavaExtractor(JAVA_FILE_PATH)
        self.xml_reader = XMLReader(XML_FILE_PATH)
        self.client = OpenAIClient()
        self.test_result_headers = []
        self.target_classes = ["NumberTool", "TextTool", "ParameterTool"]

    def generate_singe_test(self, class_name, method_names):
        if class_name in ["NumberTool", "TextTool"]:
            prompt_str = (
                PROMPT12.replace("[CLASSNAME]", class_name)
                .replace(
                    "[MYEXPECTEDMETHODS]",
                    self.reformat_desired_methods(class_name, method_names),
                )
                .replace("[STARTINDEX]", str(len(self.test_cases) + 1))
            )
        else:
            prompt_str = (
                PROMPT3.replace("[CLASSNAME]", class_name)
                .replace(
                    "[MYEXPECTEDMETHODS]",
                    self.reformat_desired_methods(class_name, method_names),
                )
                .replace("[STARTINDEX]", str(len(self.test_cases) + 1))
            )
        add_to_file("Prompt>:\n" + prompt_str, LOG_PATH)
        for _ in range(MAX_RETRY):
            response = self.client.chat(
                model="gpt-4o-mini",
                messages=[
                    {"role": "system", "content": SYSTEM_PROMPT},
                    {"role": "user", "content": prompt_str},
                ],
            ).content
            add_to_file("Response>:\n" + response, LOG_PATH)
            try:
                imports, test_cases = TestCaseExtractor().extract_test_cases(response)
                current_name_list = [tc["name"] for tc in self.test_cases]
                for test_case in test_cases:
                    # 防止有重复名字的test case
                    if test_case["name"] in current_name_list:
                        continue
                    self.test_cases.append(test_case)
                    current_name_list.append(test_case["name"])
                self.test_result_headers += imports
            except Exception as e:
                print(e)
                continue

    # 生成测试用例总体流程
    # 1. 按照覆盖率rule，获取要覆盖的函数
    # 2. 生成测试用例
    def generate(self):
        for _ in range(TEST_TIME_LIMIT):
            class_name = self.target_classes[random.randint(0, 2)]
            method_names = self.get_method_names_by_rule(class_name)
            if len(method_names) == 0:
                print("No method to cover")
                class_name, method_names = self.get_random_method_to_cover(class_name)
            self.generate_singe_test(class_name, method_names)
            if self.can_stop_generate():
                break

        write_to_file(self.format_results(), RESULT_PATH)

    # 优先把没有覆盖率的函数全部覆盖
    def get_method_names_by_rule(self, class_name):
        test_methods = []
        choose_first = random.randint(0, 1) == 0
        print("choose_first", choose_first)
        print("class_name", class_name)
        if choose_first and len(self.xml_reader.coverage_method_queue) > 0:
            for _ in range(random.randint(1, EVERY_TEST_METHOD_LIMIT)):
                for method in self.xml_reader.coverage_method_queue:
                    if (
                        self.class_has_method(class_name, method["name"])
                        and method["coverage"] < 0.9
                    ):
                        test_methods.append(method)
                        self.xml_reader.coverage_method_queue.remove(method)
                        break
        elif not choose_first and len(self.xml_reader.coverline_method_queue) > 0:
            for _ in range(random.randint(1, EVERY_TEST_METHOD_LIMIT)):
                for method in self.xml_reader.coverline_method_queue:
                    if (
                        self.class_has_method(class_name, method["name"])
                        and method["missed"] > 5
                    ):
                        test_methods.append(method)
                        self.xml_reader.coverline_method_queue.remove(method)
                        break
        test_methods = list(tuple(test_methods))

        print("test_methods", test_methods)
        return test_methods

    # 如果没有覆盖率小于0.95的函数，随机选择一个函数覆盖
    def get_random_method_to_cover(self, class_name):
        new_class = random.choice(self.target_classes)
        while new_class == class_name:
            new_class = random.choice(self.target_classes)
        method_names = self.get_method_names_by_rule(new_class)
        if len(method_names) > 0:
            return new_class, method_names
        else:
            method_names = [
                {"name": name}
                for name in self.java_extractor.class_dict[new_class].keys()
            ]
            return new_class, random.choices(
                method_names, k=random.randint(1, EVERY_TEST_METHOD_LIMIT)
            )

    # 终止条件：所有函数都覆盖 OR Test_Case数量达到上限
    def can_stop_generate(self):
        return len(self.test_cases) >= TEST_CASE_LIMIT or self.is_all_method_covered()

    def class_has_method(self, class_name, method_name):
        # print("method_name", method_name)
        # print(list(self.java_extractor.class_dict[class_name].keys()))
        return method_name in list(self.java_extractor.class_dict[class_name].keys())

    def is_all_method_covered(self):
        return (
            len(self.xml_reader.coverage_method_queue)
            + len(self.xml_reader.coverline_method_queue)
            == 0
        )

    def reformat_desired_methods(
        self, class_name: str, desired_methods: list[str]
    ) -> str:
        method_dict = {}
        for sub_class_name in self.target_classes:
            method_dict[sub_class_name] = []
        # print(self.java_extractor.class_dict)
        method_definitions = self.java_extractor.class_dict[class_name]
        print("desired_methods", desired_methods)
        for method_cover in desired_methods:
            if method_cover["name"] in method_definitions:

                for called_by_names in method_definitions[method_cover["name"]][
                    "called_by"
                ]:
                    method_dict[class_name].append(
                        method_definitions[called_by_names]["body"]
                    )
                method_dict[class_name].append(
                    method_definitions[method_cover["name"]]["body"]
                )
        result_list = []
        for class_name, methods in method_dict.items():
            if len(methods) == 0:
                continue
            methods_str = "\n\t\t".join(methods)
            if class_name == "ParameterTool":
                # methods_str = PARAMETERS + methods_str
                pass
            class_fields_str = CLASS_FORMAT.replace("[CLASS_NAME]", class_name).replace(
                "[METHODS]", methods_str
            )
            result_list.append(class_fields_str)

        return MAIN_CLASS_FORMAT.replace("[CLASSFIELDS]", "\n\n".join(result_list))

    def format_results(self):
        imports = list(set(self.test_result_headers))
        imports = "\n".join(imports)
        test_cases = "\n".join(["\t@Test\n" + tc["body"] for tc in self.test_cases])
        return RESULT_FORMAT.replace("[IMPORTS]", imports).replace(
            "[TESTCASES]", test_cases
        )


if __name__ == "__main__":
    write_to_file("", LOG_PATH)
    testCaseGenerator = TestCaseGenerator()
    testCaseGenerator.generate()
