package bot;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringContext implements ApplicationContextAware {

    private static ApplicationContext context;

    public static <T> T getBean(Class<T> beanClass) {
        return context.getBean(beanClass);
    }

    public static <T> List<T> getBeansOfType(Class<T> parent) {
        List<T> beans = new ArrayList<>();
        String[] beanNames = context.getBeanNamesForType(parent);

        for (String beanName : beanNames) {
            beans.add(context.getBean(beanName, parent));
        }

        return beans;
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        SpringContext.context = context;
    }
}
