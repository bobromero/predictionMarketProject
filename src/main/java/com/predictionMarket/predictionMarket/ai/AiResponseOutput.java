package com.predictionMarket.predictionMarket.ai;

import java.util.List;

public class AiResponseOutput {
    private List<AiResponseContent> content;

    public List<AiResponseContent> getContent() { return content; }
    public void setContent(List<AiResponseContent> content) { this.content = content; }

    public String getText() {
        if (content != null && !content.isEmpty()) {
            return content.get(0).getText();
        }
        return null;
    }
}
