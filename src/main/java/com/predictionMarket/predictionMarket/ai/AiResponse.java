package com.predictionMarket.predictionMarket.ai;

import java.util.List;

public class AiResponse {
    private List<AiResponseOutput> output;

    public List<AiResponseOutput> getOutput() { return output; }
    public void setOutput(List<AiResponseOutput> output) { this.output = output; }

    // Convenience method to get the text directly
    public String getText() {
        if (output != null && !output.isEmpty()) {
            return output.get(0).getText();
        }
        return null;
    }

}
