需要完整源码联系qq 1368289694




音频处理工具


WaveMerge 是一个基于 Spring Boot 的音频处理工具，提供了音频合并、混音等功能。该项目使用 FFmpeg 作为底层音频处理引擎，支持 MP3 和 WAV 格式的音频文件。

## 主要功能

### 1. 音频合并 (Audio Merge)
- 支持多个音频文件的合并
- 支持 MP3 和 WAV 格式
- 自动处理音频格式转换
- 保持音频质量

**接口信息：**
```
POST /api/audio/merge
```

**请求参数：**
```json
{
    "audioPaths": ["音频文件路径1", "音频文件路径2", ...],
    "outType": "mp3/wav"  // 输出格式
}
```

**返回结果：**
```json
{
    "code": 200,
    "msg": "操作成功",
    "data": {
        "outputFile": "输出文件路径",
        "audioDuration": "音频时长"
    }
}
```

### 2. 音频混音 (Audio Mix)
- 支持主音频和背景音频的混音
- 可设置混音周期
- 支持音量调节
- 支持 MP3 和 WAV 格式

**接口信息：**
```
POST /api/audio/mix
```

**请求参数：**
```json
{
    "mainAudio": "主音频文件路径",
    "backgroundAudio": "背景音频文件路径",
    "outType": "mp3/wav",  // 输出格式
    "mixPeriod": 20  // 混音周期（秒）
}
```

**返回结果：**
```json
{
    "code": 200,
    "msg": "操作成功",
    "data": {
        "outputFile": "输出文件路径",
        "audioDuration": "音频时长"
    }
}
```

## 技术特点

1. **音频处理**
   - 使用 FFmpeg 进行音频处理
   - 支持多种音频格式转换
   - 保持音频质量

2. **错误处理**
   - 完善的参数验证
   - 详细的错误提示
   - 异常捕获和处理

3. **性能优化**
   - 使用临时目录处理中间文件
   - 自动清理临时文件
   - 异步处理大文件

## 使用要求

1. **环境要求**
   - JDK 1.8 或以上
   - FFmpeg 已安装并配置环境变量
   - Spring Boot 2.x

2. **配置说明**
   - 临时文件目录配置
   - 输出文件目录配置
   - 音频处理参数配置

## 注意事项

1. 输入文件必须存在且可访问
2. 支持的音频格式：MP3、WAV
3. 输出目录必须具有写入权限
4. 大文件处理可能需要较长时间
5. 确保系统已正确安装 FFmpeg

## 错误处理

常见错误码及说明：
- 400: 请求参数错误
- 404: 文件不存在
- 500: 服务器内部错误

## 示例

### 音频合并示例
```bash
curl -X POST http://localhost:8080/api/audio/merge \
-H "Content-Type: application/json" \
-d '{
    "audioPaths": ["/path/to/audio1.mp3", "/path/to/audio2.wav"],
    "outType": "mp3"
}'
```

### 音频混音示例
```bash
curl -X POST http://localhost:8080/api/audio/mix \
-H "Content-Type: application/json" \
-d '{
    "mainAudio": "/path/to/main.mp3",
    "backgroundAudio": "/path/to/background.wav",
    "outType": "wav",
    "mixPeriod": 20
}'
```

## 许可证

[添加许可证信息]

## 作者

[添加作者信息]
