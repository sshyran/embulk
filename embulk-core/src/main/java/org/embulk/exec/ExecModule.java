package org.embulk.exec;

import com.google.common.base.Preconditions;
import com.google.inject.Module;
import com.google.inject.name.Names;
import com.google.inject.Binder;
import com.google.inject.Scopes;
import com.fasterxml.jackson.module.guice.ObjectMapperModule;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import org.embulk.time.TimestampFormatConfigSerDe;
import org.embulk.time.DateTimeZoneSerDe;
import org.embulk.config.ModelManager;
import org.embulk.record.TypeManager;
import org.embulk.config.DataSourceSerDe;
import org.embulk.config.EnumTaskSerDe;
import org.embulk.spi.ParserPlugin;
import org.embulk.spi.CharsetSerDe;

public class ExecModule
        implements Module
{
    @Override
    public void configure(Binder binder)
    {
        Preconditions.checkNotNull(binder, "binder is null.");

        binder.bind(ModelManager.class).in(Scopes.SINGLETON);
        binder.bind(TypeManager.class).asEagerSingleton();
        binder.bind(BufferManager.class).in(Scopes.SINGLETON);

        // GuessExecutor
        binder.bind(ParserPlugin.class).annotatedWith(Names.named("system_guess"))
            .to(GuessExecutor.GuessParserPlugin.class);
        binder.bind(ParserPlugin.class).annotatedWith(Names.named("system_sampling"))
            .toInstance(new SamplingParserPlugin(32*1024));  // TODO get sample size from system config

        // serde
        binder.bind(TimestampFormatConfigSerDe.class).asEagerSingleton();

        ObjectMapperModule mapper = new ObjectMapperModule();
        DataSourceSerDe.configure(mapper);
        DateTimeZoneSerDe.configure(mapper);
        EnumTaskSerDe.configure(mapper);
        CharsetSerDe.configure(mapper);
        mapper.registerModule(new GuavaModule());  // jackson-datatype-guava
        mapper.registerModule(new JodaModule());  // jackson-datatype-joda
        mapper.configure(binder);
    }
}