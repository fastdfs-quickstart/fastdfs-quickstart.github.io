package org.csource.quickstart.processor;


import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.csource.quickstart.annotation.ProcessorOrder;
import org.csource.quickstart.util.AopTargetUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;


public class FileProcessFactory
    implements
      FactoryBean<Object>,
      ApplicationListener<ContextRefreshedEvent>,
      DisposableBean {

  private List<FileUploadProcessor> registFileProcessList = new LinkedList<FileUploadProcessor>();

  @Override
  public Object getObject() throws Exception {
    return registFileProcessList;
  }

  @Override
  public Class<?> getObjectType() {
    return Object.class;
  }

  @Override
  public boolean isSingleton() {
    return true;
  }

  @Override
  public void onApplicationEvent(ContextRefreshedEvent event) {
    // Spring context initialized trigger
    if (event.getApplicationContext().getParent() == null) {
      loadProcessor(event);
    }

    // spring web root initialized trigger
    if (event.getApplicationContext().getDisplayName().equals("Root WebApplicationContext")) {
      if (registFileProcessList.size()==0) {
        loadProcessor(event);
      }
    }
  }

  private void loadProcessor(ContextRefreshedEvent event) {
    ApplicationContext ac = event.getApplicationContext();
    Map<String, FileUploadProcessor> beans = ac.getBeansOfType(FileUploadProcessor.class);
    if (beans != null && beans.size() > 0) {
      Map<Integer, LinkedList<FileUploadProcessor>> temp = new TreeMap<Integer, LinkedList<FileUploadProcessor>>();

      // order processor
      for (Map.Entry<String, FileUploadProcessor> entry : beans.entrySet()) {
        FileUploadProcessor processor = entry.getValue();
        Integer registOrder = Integer.MAX_VALUE;
        try {
          FileUploadProcessor target = (FileUploadProcessor) AopTargetUtils.getTarget(processor);
          ProcessorOrder processorOrder = target.getClass().getAnnotation(ProcessorOrder.class);
          if (processorOrder != null) {
            registOrder = processorOrder.order();
          }
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
        LinkedList<FileUploadProcessor> storage = temp.get(registOrder);
        if (storage == null) {
          storage = new LinkedList<FileUploadProcessor>();
          temp.put(registOrder, storage);
        }
        storage.add(processor);
      }
      // linked
      for (Entry<Integer, LinkedList<FileUploadProcessor>> orderProcessorEntry : temp.entrySet()) {
        registFileProcessList.addAll(orderProcessorEntry.getValue());
      }
    }
  }

  @Override
  public void destroy() throws Exception {
    this.registFileProcessList = null;
  }


}
