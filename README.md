# AI 智能体开发 - 使用SpringAI构建智能体

## 项目简介

本项目是《计算机科学与技术专业》课程的教学案例项目- **使用SpringAI构建智能体**，基于 **Spring AI Alibaba** + **DashScope** + **Ollama** 构建完整的 AI 智能体应用。

**技术栈：**
- Spring Boot 3.2.10
- Spring AI 1.0.0
- Spring AI Alibaba 1.0.0.4（DashScope Starter）
- Java 21
- Maven

---

## 教学计划对应关系

| 周次      | 教学单元 | 实验内容 | 对应代码模块 |
|---------|----------|----------|--------------|
| **第1周** |
| 1-2     | Ollama 本地部署 | DeepSeek/Qwen 模型本地部署；Java 调用 Ollama API | `AiConfig.java` (ollamaChatClient) |
| 3-4     | Spring AI Alibaba 入门 | 项目初始化与配置；获取 DashScope API Key | `pom.xml`、`application.yml`、`AiConfig.java` |
| 5-6     | DeepSeek 聊天接口接入 | 多轮对话、文生图、语音合成 | `AssistantController.java`、`ImageService.java`、`SpeechService.java` |
| 7-8     | Spring AI 核心 API | ChatModel、ChatClient；同步/流式响应 | `AiConfig.java`、`AssistantController.java` (/chat/stream) |
| 9-10    | 结构化输出与 Prompt | 结构化输出、Prompt 模板、Advisor 会话记忆 | `ConversationService.java`、`ChatMessage.java` |
| 11-12   | Function Calling | 函数定义与注册；天气查询、计算器工具调用 | `ToolService.java`、`AssistantController.java` (/tools/**) |
| **第2周** |
| 13-14   | RAG 与 Milvus 入门 | RAG 工作流程；Milvus 安装 (Docker Compose)；Attu 可视化 | `RagController.java`、`KnowledgeBaseService.java` |
| 15-16   | Milvus Java SDK + Spring AI 整合 | Milvus SDK 使用；Spring AI 与 Milvus 整合；文档向量化 | `RagController.java` (/document, /search, /ask) |
| 17-18   | AI 网站助手项目初始化 | 环境搭建、项目结构设计 | 项目整体结构 |
| 19-20   | AI 网站助手后端开发 | 智能问答、工具调用、后端接口 | `AssistantController.java`、`WebsiteAssistantService.java` |
| 21-22   | AI 网站助手前端开发 | 流式响应、SSE 流式渲染、前端交互 | `AssistantController.java` (/chat/stream) |
| 23-24   | 企业级 RAG 知识库 | MySQL+Milvus 双库设计；文档管理 | `RagController.java`、`KnowledgeBaseService.java` |
| 25-26   | RAG 全流程整合 | 知识搜索、智能问答、RAG 全流程 | `RagController.java` (/ask) |
| 27      | 项目测试与优化 | 前端开发、测试、Bug 修复 | 全项目 |
| **第3周** |
| 28      | 项目测试与优化 | 前端开发、测试、Bug 修复 | 全项目 |
| 29-30   | 项目答辩 | 小组项目答辩、功能演示、考核评分 | 全项目 |

---

## 项目结构

```
ai-demo/
├── src/main/java/org/example/aidemo/
│   ├── AiDemoApplication.java          # 启动类
│   ├── config/
│   │   └── AiConfig.java               # AI 配置（ChatClient、语音合成）
│   ├── assistant/
│   │   └── AssistantController.java    # AI 助手主控制器（统一接口）
│   ├── rag/
│   │   └── RagController.java          # RAG 知识库控制器
│   ├── model/                          # 数据模型
│   │   ├── ChatMessage.java
│   │   ├── Conversation.java
│   │   ├── CalculatorRequest.java
│   │   ├── ImageRequest.java
│   │   ├── KnowledgeDocument.java
│   │   ├── SearchResult.java
│   │   ├── SpeechRequest.java
│   │   └── WeatherInfo.java
│   └── service/                        # 业务服务层
│       ├── ConversationService.java    # 会话管理
│       ├── ImageService.java           # 文生图
│       ├── SpeechService.java          # 语音合成
│       ├── ToolService.java            # 工具调用（天气、计算器）
│       ├── WebsiteAssistantService.java # 网站助手
│       └── KnowledgeBaseService.java   # 知识库管理
└── src/main/resources/
    └── application.yml                 # 配置文件
```

---

## 环境配置

### 1. 环境要求
- JDK 21
- Maven 3.8+
- DashScope API Key（阿里云灵积）

### 2. 获取 DashScope API Key
1. 访问 [DashScope 开放平台](https://dashscope.aliyun.com/)
2. 注册/登录阿里云账号
3. 进入「API-KEY」页面，创建新的 API Key
4. 将 API Key 填入 `application.yml`

### 3. 配置文件

```yaml
spring:
  application:
    name: ai-demo
  ai:
    ollama:
      base-url: http://localhost:11434
      chat:
        options:
          model: qwen2.5:3b
          temperature: 0.7

    dashscope:
      api-key: sk-xxxxxxxxxxxxxxxx  # 替换为你的 API Key
      chat:
        options:
          model: qwen-plus
          temperature: 0.7

server:
  port: 8080
```

---

## 接口文档

### 基础信息
- **Base URL**: `http://localhost:8080`
- **Content-Type**: `application/json`

---

### 一、基础聊天 (`/api/assistant`)

#### 1. 基础聊天（默认模型）
```http
POST /api/assistant/chat
Content-Type: application/json

{
  "message": "你好，请介绍一下 Spring AI"
}
```
**响应：**
```json
{
  "response": "Spring AI 是 Spring 官方提供的 AI 开发框架..."
}
```

#### 2. 带系统提示的聊天
```http
POST /api/assistant/chat/with-system
Content-Type: application/json

{
  "message": "你好",
  "systemPrompt": "你是一个专业的Java开发助手"
}
```

#### 3. DashScope 模型聊天
```http
POST /api/assistant/dashscope/chat
Content-Type: application/json

{
  "message": "你好",
  "systemPrompt": "你是一个有用的AI助手"
}
```

#### 4. Ollama 本地模型聊天
```http
POST /api/assistant/ollama/chat
Content-Type: application/json

{
  "message": "你好",
  "systemPrompt": "你是一个有用的AI助手"
}
```

#### 5. Ollama 本地模型流式聊天
```http
POST /api/assistant/ollama/stream
Content-Type: application/json

{
  "message": "你好"
}
```
**响应：** Server-Sent Events (SSE) 流式返回

---

### 二、会话管理 (`/api/assistant`)

#### 1. 获取所有会话列表
```http
GET /api/assistant/conversations
```

#### 2. 创建会话
```http
POST /api/assistant/conversation
```
**响应：**
```json
{
  "conversationId": "uuid-string",
  "model": "dashscope"
}
```

#### 3. 多轮对话（带历史）
```http
POST /api/assistant/conversation/{conversationId}/chat
Content-Type: application/json

{
  "message": "你好，请介绍一下 Spring AI"
}
```
**响应：**
```json
{
  "response": "Spring AI 是 Spring 官方提供的 AI 开发框架...",
  "conversationId": "uuid-string"
}
```

#### 4. 获取会话历史
```http
GET /api/assistant/conversation/{conversationId}
```

#### 5. 删除会话
```http
DELETE /api/assistant/conversation/{conversationId}
```

---

### 三、文生图 (`/api/assistant/image`)

#### 1. 文生图
```http
POST /api/assistant/image/generate
Content-Type: application/json

{
  "prompt": "一只可爱的猫咪在草地上玩耍"
}
```
**响应：**
```json
{
  "prompt": "一只可爱的猫咪在草地上玩耍",
  "imageUrl": "https://dashscope-result-xxx.oss-cn-xxx.aliyuncs.com/..."
}
```

#### 2. 文生图（优化提示词）
```http
POST /api/assistant/image/generate/enhanced
Content-Type: application/json

{
  "prompt": "未来城市"
}
```

---

### 四、语音合成 (`/api/assistant/speech`)

#### 文本转语音
```http
POST /api/assistant/speech/synthesize
Content-Type: application/json

{
  "text": "你好，欢迎使用 AI 智能助手",
  "voice": "alloy",      // 可选：alloy, echo, fable, onyx, nova, shimmer
  "speed": 1.0           // 可选：语速 0.5-2.0
}
```
**响应：** 音频文件流（MP3 格式）

---

### 五、工具调用 (`/api/assistant/tools`)

#### 1. 天气查询
```http
GET /api/assistant/tools/weather?city=南京
```
**响应：**
```json
{
  "city": "南京",
  "result": "【天气查询结果】\n城市: 南京\n天气: 多云转晴\n温度: 23°C\n湿度: 65%\n风力: 东北风3级"
}
```

#### 2. 计算器
```http
GET /api/assistant/tools/calculate?expression=12*(3+4)
```
**响应：**
```json
{
  "expression": "12*(3+4)",
  "result": "计算结果: 12*(3+4) = 84.0000"
}
```

#### 3. 智能工具调用（AI 判断）
```http
POST /api/assistant/tools/smart
Content-Type: application/json

{
  "message": "北京今天天气怎么样？"
}
```
**响应：**
```json
{
  "input": "北京今天天气怎么样？",
  "result": "【天气查询结果】\n城市: 北京\n天气: 晴\n温度: 28°C..."
}
```

---

### 六、网站助手 (`/api/assistant/website`)

#### 网站助手问答
```http
POST /api/assistant/website/chat
Content-Type: application/json

{
  "query": "如何使用这个系统？"
}
```
**响应：**
```json
{
  "response": "您好！我是网站智能助手，很高兴为您服务..."
}
```

---

### 七、流式对话 (`/api/assistant/chat/stream`)

```http
POST /api/assistant/chat/stream
Content-Type: application/json

{
  "message": "请详细介绍一下 RAG 技术"
}
```
**响应：** Server-Sent Events (SSE) 流式返回

---

### 八、RAG 知识库 (`/api/rag`)

#### 1. 上传文档（文本）
```http
POST /api/rag/document
Content-Type: application/json

{
  "title": "Spring AI 教程",
  "content": "Spring AI 是 Spring 官方提供的 AI 开发框架..."
}
```

#### 2. 上传文件
```http
POST /api/rag/document/upload
Content-Type: multipart/form-data

file: [文件]
```

#### 3. 知识搜索
```http
GET /api/rag/search?query=Spring AI
```
**响应：**
```json
[
  {
    "id": "result_0",
    "content": "Spring AI 是 Spring 官方提供的 AI 开发框架...",
    "score": 1.0,
    "source": "knowledge_base"
  }
]
```

#### 4. 智能问答（RAG）
```http
POST /api/rag/ask
Content-Type: application/json

{
  "question": "Spring AI 是什么？"
}
```
**响应：**
```json
{
  "answer": "根据知识库，Spring AI 是 Spring 官方提供的 AI 开发框架...",
  "sources": ["Spring AI 是 Spring 官方提供的 AI 开发框架..."],
  "success": true
}
```

#### 5. 知识库统计
```http
GET /api/rag/stats
```
**响应：**
```json
{
  "documentCount": 5,
  "timestamp": "2026-06-07T20:30:00"
}
```

---

## 核心功能说明

### 1. 多模型支持
- **DashScope（云端）**：默认模型，支持 qwen-turbo、qwen-plus、qwen-max 等
- **Ollama（本地）**：支持 DeepSeek、Qwen2.5 等本地模型

### 2. 多模态能力
- **文本对话**：基础聊天、多轮对话、流式响应
- **文生图**：基于 DashScope 的图片生成能力
- **语音合成**：文本转语音（MP3 格式）

### 3. Function Calling
- **天气查询**：模拟天气数据查询
- **计算器**：安全表达式计算（支持四则运算）
- **智能工具调用**：AI 自动判断调用哪个工具

### 4. RAG 知识库
- **文档管理**：支持文本和文件上传
- **知识搜索**：基于关键词的相似度搜索
- **智能问答**：结合知识库内容的增强生成

### 5. 会话管理
- 多轮对话历史记录
- 会话创建、查询、删除
- 支持不同模型切换

---

## 运行项目

### 1. 克隆项目
```bash
git clone <repository-url>
cd ai-demo
```

### 2. 配置 API Key
编辑 `src/main/resources/application.yml`，填入你的 DashScope API Key。

### 3. 启动项目
```bash
./mvnw spring-boot:run
```

### 4. 访问接口
项目启动后，访问 `http://localhost:8080` 即可使用各接口。

---

## 接口速查表

| 功能 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 基础聊天 | POST | `/api/assistant/chat` | 默认模型聊天 |
| 带系统提示聊天 | POST | `/api/assistant/chat/with-system` | 自定义系统提示词 |
| DashScope 聊天 | POST | `/api/assistant/dashscope/chat` | 使用 DashScope 模型 |
| Ollama 聊天 | POST | `/api/assistant/ollama/chat` | 使用本地 Ollama 模型 |
| Ollama 流式 | POST | `/api/assistant/ollama/stream` | 本地模型 SSE 流式 |
| 获取会话列表 | GET | `/api/assistant/conversations` | 所有会话 |
| 创建会话 | POST | `/api/assistant/conversation` | 创建新会话 |
| 多轮对话 | POST | `/api/assistant/conversation/{id}/chat` | 带历史的对话 |
| 获取会话 | GET | `/api/assistant/conversation/{id}` | 会话详情 |
| 删除会话 | DELETE | `/api/assistant/conversation/{id}` | 删除会话 |
| 文生图 | POST | `/api/assistant/image/generate` | 根据文本生成图片 |
| 文生图（优化） | POST | `/api/assistant/image/generate/enhanced` | 优化提示词后生成 |
| 语音合成 | POST | `/api/assistant/speech/synthesize` | 文本转语音 MP3 |
| 天气查询 | GET | `/api/assistant/tools/weather?city=xxx` | 查询天气 |
| 计算器 | GET | `/api/assistant/tools/calculate?expr=xxx` | 数学计算 |
| 智能工具调用 | POST | `/api/assistant/tools/smart` | AI 自动判断工具 |
| 网站助手 | POST | `/api/assistant/website/chat` | 网站助手问答 |
| 流式对话 | POST | `/api/assistant/chat/stream` | SSE 流式响应 |
| 上传文档 | POST | `/api/rag/document` | 上传文本文档 |
| 知识搜索 | GET | `/api/rag/search?query=xxx` | 搜索知识库 |
| RAG 问答 | POST | `/api/rag/ask` | 基于知识库的问答 |
| 知识库统计 | GET | `/api/rag/stats` | 文档数量统计 |

---

## 注意事项

1. **API Key 安全**：不要将 API Key 提交到版本控制系统
2. **Milvus 可选**：当前版本使用内存知识库，Milvus 可根据教学进度按需启用
3. **Ollama 可选**：本地模型需要单独安装 Ollama 并下载模型
4. **DashScope 配额**：免费版有调用频率限制，请注意控制请求量

---

---

## 常见问题

### 1. DashScope API 调用失败
- 检查 API Key 是否正确
- 检查网络连接（需要访问阿里云服务）
- 查看 DashScope 控制台的配额和余额

### 2. Ollama 连接失败
- 确保 Ollama 服务已启动：`ollama serve`
- 检查模型是否已下载：`ollama list`
- 确认端口 11434 未被占用

### 3. Milvus 连接失败
- 确保 Docker 和 Docker Compose 已安装
- 检查 Milvus 容器是否正常运行：`docker ps`
- 确认端口 19530 未被占用

---

## 后续功能扩展

1. **接入真实天气 API**：将 `ToolService.queryWeather()` 替换为真实天气 API（如和风天气）
2. **MySQL 持久化**：将会话和知识库数据存储到 MySQL
3. **前端页面**：使用 Vue/React 开发前端交互界面
4. **用户认证**：添加 Spring Security 实现用户登录和权限控制
5. **更多工具**：扩展 Function Calling，添加更多实用工具

---
