package org.noixdecoco.app.command.manager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.noixdecoco.app.command.CoconutCommand;
import org.noixdecoco.app.command.annotation.Command;
import org.noixdecoco.app.dto.EventType;
import org.noixdecoco.app.dto.SlackRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Scope;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class CoconutCommandManager {

    private static final Logger LOGGER = LogManager.getLogger(CoconutCommandManager.class);
    public static final String BASE_COMMAND_PACKAGE = "org.noixdecoco.app.command";

    @Autowired
    private AutowireCapableBeanFactory beanFactory;

    private static final Map<EventType, Map<Predicate<SlackRequestDTO>, Method>> ALL_COMMANDS = new EnumMap(EventType.class);

    @PostConstruct
    private void initialiseCommands() {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);

        scanner.addIncludeFilter(new AnnotationTypeFilter(Command.class));

        LOGGER.info("Looking for commands in package: " + BASE_COMMAND_PACKAGE);
        for (BeanDefinition bd : scanner.findCandidateComponents(BASE_COMMAND_PACKAGE)) {
            LOGGER.info("Found command: " + bd.getBeanClassName());
            try {
                Class<?> command = Class.forName(bd.getBeanClassName());
                Command commandAnnotation = command.getAnnotation(Command.class);
                CoconutCommandManager.registerCommand(commandAnnotation.value(),
                        (Predicate<SlackRequestDTO>) command.getMethod("getPredicate").invoke(null),
                        command.getMethod("build", SlackRequestDTO.class));
            } catch (ClassNotFoundException e) {
                LOGGER.error("Failed to find class: " + bd.getBeanClassName());
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                LOGGER.error("Command " + bd.getBeanClassName() + " needs to implement static methods getPredicate() and build(SlackRequestDTO request)");
            }

        }
    }

    public static void registerCommand(EventType event, Predicate<SlackRequestDTO> predicate, Method commandBuilderMethod) {
        ALL_COMMANDS.computeIfAbsent(event, k -> new HashMap<>());
        ALL_COMMANDS.get(event).put(predicate, commandBuilderMethod);
    }

    public CoconutCommand buildFromRequest(SlackRequestDTO request) {
        CoconutCommand command = null;
        if (request != null && request.getEvent() != null) {
            Map<Predicate<SlackRequestDTO>, Method> commands = ALL_COMMANDS.get(EventType.fromString(request.getEvent().getType()));
            for (Map.Entry<Predicate<SlackRequestDTO>, Method> entry : commands.entrySet()) {
                if (entry.getKey().test(request)) {
                    try {
                        command = (CoconutCommand) entry.getValue().invoke(null, request);
                        beanFactory.autowireBean(command);
                        break;
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        LOGGER.error("Failed to build coconut command when calling build", e);
                    }
                }
            }
        }
        return command;
    }
}
