import copy
import xml.etree.ElementTree as ET

# 调用函数解析XML文件，替换成你的XML文件路径


class XMLReader:
    def __init__(self, path):
        self.path = path
        self.main_class_name = "Subject"
        self.status_dict = {}
        self.sub_class_names = ["NumberTool", "ParameterTool", "TextTool"]
        self.coverage_method_queue = []
        self.coverline_method_queue = []
        # RANK 规则：1. 优先找没有coverage的函数 2. 其次找没有coverage行数最多的函数
        self.parse_xml(path)
        # print(xmlReader.status_dict)
        self.sort_coverage_queue()

    # 递归函数：用于遍历XML中的每个元素
    def parse_element(self, element):
        if self.is_sub_class_name(element):
            element_dict = self.parse_class_element(element)
            self.status_dict[element.get("name")] = element_dict
            # print(element_dict)
        # 递归遍历子元素
        for child in element:
            self.parse_element(child)

    def parse_class_element(self, element):
        element_dict = {}
        for child in element:
            if child.tag == "counter":
                element_dict[f"CLASS_{child.get('type')}"] = {
                    "covered": child.get("covered"),
                    "missed": child.get("missed"),
                    "coverage": int(child.get("covered"))
                    / (int(child.get("covered")) + int(child.get("missed"))),
                }
            if child.tag == "method":
                method_dict = self.parse_method_element(child)
                element_dict[method_dict["name"]] = method_dict
        return element_dict

    def is_sub_class_name(self, element):
        for sub_class_name in self.sub_class_names:
            if element.get("name") == f"{self.main_class_name}${sub_class_name}":
                return True
        return False

    def parse_method_element(self, element):
        method_dict = {}
        for child in element:
            if child.tag == "counter":
                coverage = int(child.get("covered")) / (
                    int(child.get("covered")) + int(child.get("missed"))
                )

                method_dict[f"{child.get('type')}"] = {
                    "covered": child.get("covered"),
                    "missed": child.get("missed"),
                    "coverage": coverage,
                }
                exist = False
                for method in self.coverage_method_queue:
                    if method["name"] == element.get("name"):
                        exist = True
                        break
                if not exist:
                    self.coverage_method_queue.append(
                        {"name": element.get("name"), "coverage": coverage}
                    )
                self.coverline_method_queue.append(
                    {
                        "name": element.get("name"),
                        "missed": int(child.get("missed")),
                    }
                )
        method_dict["name"] = element.get("name")
        return method_dict

    # 读取并解析XML文件
    def parse_xml(self, file_path):
        tree = ET.parse(file_path)  # 读取XML文件
        root = tree.getroot()  # 获取根元素
        self.parse_element(root)  # 递归解析根元素

    def sort_coverage_queue(self):
        # 名字相同的函数，只保留覆盖率最高的
        coverage_method_queue = []
        coverline_method_queue = []
        for method_info in self.coverage_method_queue:
            exist = False
            for method in coverage_method_queue:
                if method["name"] == method_info["name"]:
                    exist = True
                    if method_info["coverage"] > method["coverage"]:
                        method["coverage"] = method_info["coverage"]
                    break
            if not exist:
                coverage_method_queue.append(method_info)

        for method_info in self.coverline_method_queue:
            exist = False
            for method in coverline_method_queue:
                if method["name"] == method_info["name"]:
                    exist = True
                    if method_info["missed"] > method["missed"]:
                        method["missed"] = method_info["missed"]
                    break
            if not exist:
                coverline_method_queue.append(method_info)

        self.coverage_method_queue = coverage_method_queue
        self.coverline_method_queue = coverline_method_queue

        self.coverage_method_queue.sort(key=lambda x: x["coverage"], reverse=False)
        self.coverline_method_queue.sort(key=lambda x: x["missed"], reverse=True)
        self.coverline_method_queue = [
            method_info
            for method_info in self.coverline_method_queue
            if method_info["missed"] > 0
        ]


if __name__ == "__main__":
    xmlReader = XMLReader("./target/24Assign1all.xml")
    xmlReader.parse_xml(xmlReader.path)
    # print(xmlReader.status_dict)
    xmlReader.sort_coverage_queue()
    print(xmlReader.coverage_method_queue[:10])
    print(xmlReader.coverline_method_queue[:10])

    xmlReader = XMLReader("./target/24Assign1all2.xml")
    xmlReader.parse_xml(xmlReader.path)
    # print(xmlReader.status_dict)
    xmlReader.sort_coverage_queue()
    print(xmlReader.coverage_method_queue[:10])
    print(xmlReader.coverline_method_queue[:10])
