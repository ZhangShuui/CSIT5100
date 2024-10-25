from abc import ABC
from openai import OpenAI
from CONSTANT import *


class LLM(ABC):
    def __init__(self) -> None:
        self.model = None
        self.client = None
        self.temprature = None
        self.max_tokens = None

    def chat(self, messages, model):
        pass


class OpenAIClient(LLM):
    def __init__(self) -> None:
        self.api_key = OPENAI_API_KEY
        self.base_url = "https://api.aiproxy.io/v1"
        self.client = OpenAI(api_key=self.api_key, base_url=self.base_url)
        self.temprature = 0.5

    def chat(self, messages, model):
        chat_completion = self.client.chat.completions.create(
            messages=messages,
            model=model,
            temperature=self.temprature,
        )
        return chat_completion.choices[0].message


class DeepSeekClient(LLM):
    def __init__(self) -> None:
        self.api_key = DEEPSEEK_API_KEY
        self.base_url = "https://api.aiproxy.io/v1"
        self.client = OpenAI(api_key=self.api_key, base_url=self.base_url)

    def chat(self, messages, model):
        chat_completion = self.client.chat.completions.create(
            messages=messages,
            model=model,
            temperature=self.temprature,
            max_tokens=self.max_tokens,
        )
        return chat_completion.choices[0].message
