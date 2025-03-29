Requirements:
# Core Functionality:
Implement a service that analyzes financial transactions in real-time todetect potential fraud.  
Use a simple rule-based detection mechanism (e.g.,transactions exceedinga certain amount, transactions from suspicious accounts).  
Notify (e.g., log, alert) when a fraudulent transaction is detected.  
# High Availability and Resilience:
Deploy the service on a Kubernetes cluster (AWS EKS, GCP GKE, AlibabaACK).    
Ensure the service is highly available using Kubernetes features likeDeployment, Service, and Horizontal Pod Autoscaler (HPA)..    
Use message queuing services (e.g., AWS SOS, GCP Pub/Sub, AlibabaMessage Service) to handle incoming transactions.  
# Performance:
Ensure the service can handle real-time transaction data with low latency  
Implement a distributed logging mechanism using cloud-native loggingservices (e.g., AWs CloudWatch, GCP Stackdriver, Alibaba Cloud LogService).  
# Testing:
Write unit tests using JUnit.  
Write integration tests to verify the interaction with message queuing andlogging services.  
Simulate fraudulent transactions to ensure the detection mechanism workscorrectly.  
Perform resilience testing to ensure the service can recover from failures(e.g., pod restarts, node failures).  
# Documentation:
Provide a README file with instructions on how to deploy and test the service.  
Include architecture diagrams and explanations of design choices.  
# Deliverables:
Source code repository (e.g., GitHub).  
Kubernetes deployment manifests or Helm charts.  
Test coverage report.  
Resilience test results.  
Documentation.  