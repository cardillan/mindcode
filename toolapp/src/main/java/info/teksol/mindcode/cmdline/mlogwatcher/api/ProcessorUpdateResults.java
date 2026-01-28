package info.teksol.mindcode.cmdline.mlogwatcher.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ProcessorUpdateResults implements Results {

    @JsonProperty("processor_updates")
    private List<LogicProcessor> processorUpdates;

    public ProcessorUpdateResults() {
    }

    public ProcessorUpdateResults(List<LogicProcessor> processorUpdates) {
        this.processorUpdates = processorUpdates;
    }

    public List<LogicProcessor> getProcessorUpdates() {
        return processorUpdates;
    }

    public void setProcessorUpdates(List<LogicProcessor> processorUpdates) {
        this.processorUpdates = processorUpdates;
    }

}
