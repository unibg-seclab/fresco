package dk.alexandra.fresco.demo.inputsum;

import dk.alexandra.fresco.framework.Application;
import dk.alexandra.fresco.framework.BuilderFactory;
import dk.alexandra.fresco.framework.Computation;
import dk.alexandra.fresco.framework.ProtocolProducer;
import dk.alexandra.fresco.framework.builder.ProtocolBuilderHelper;
import dk.alexandra.fresco.framework.builder.ProtocolBuilderNumeric.SequentialNumericBuilder;

public abstract class DemoApplication<Output> implements
    Application<Output, SequentialNumericBuilder> {

  protected Computation<Output> output;

  public abstract ProtocolProducer prepareApplication(BuilderFactory factoryProducer);

  @Override
  public Computation<Output> prepareApplication(SequentialNumericBuilder producer) {
    producer.append(prepareApplication(ProtocolBuilderHelper.getFactory(producer)));
    return this.output;
  }

  public Computation<Output> getOutput() {
    return output;
  }
}