# wfe-distributed-integration-lib
J2EE library for integrating distributed document exchange within container-based environment which uses Camunda workflow engine.

##Goals
 - To decouple Camunda engine from settings service so it's possible to exchange BPMNs and DMNs between 2 μServices
 - To help implementing asynchronous document processing by introducing a message broker into μServices composition
 
##Examples
 - Use Vagrant project in order to run example on a local environment
 
##References
 - Camunda-Spring integration project
    - https://docs.camunda.org/manual/7.9/user-guide/spring-framework-integration/
    - https://github.com/camunda/camunda-bpm-platform/tree/master/engine-spring