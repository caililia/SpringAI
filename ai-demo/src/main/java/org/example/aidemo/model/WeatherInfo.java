package org.example.aidemo.model;

import java.time.LocalDateTime;

/**
 * 结构化输出模型 - 天气查询结果
 * 演示 Spring AI 结构化输出功能
 */
public class WeatherInfo {
    private String city;
    private String description;  // 天气描述
    private Double temperature;  // 温度（摄氏度）
    private Integer humidity;    // 湿度百分比
    private String wind;         // 风力描述
    private LocalDateTime reportTime;

    public WeatherInfo() {
        this.reportTime = LocalDateTime.now();
    }

    public WeatherInfo(String city, String description, Double temperature, Integer humidity, String wind) {
        this();
        this.city = city;
        this.description = description;
        this.temperature = temperature;
        this.humidity = humidity;
        this.wind = wind;
    }

    // Getters and Setters
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Double getTemperature() { return temperature; }
    public void setTemperature(Double temperature) { this.temperature = temperature; }
    public Integer getHumidity() { return humidity; }
    public void setHumidity(Integer humidity) { this.humidity = humidity; }
    public String getWind() { return wind; }
    public void setWind(String wind) { this.wind = wind; }
    public LocalDateTime getReportTime() { return reportTime; }
    public void setReportTime(LocalDateTime reportTime) { this.reportTime = reportTime; }

    @Override
    public String toString() {
        return String.format("城市: %s\n天气: %s\n温度: %.1f°C\n湿度: %d%%\n风力: %s\n更新时间: %s",
                city, description, temperature, humidity, wind, reportTime);
    }
}
