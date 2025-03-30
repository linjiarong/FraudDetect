# Fraud Detection Service

This project is a Spring Boot application designed for real-time analysis of financial transactions to detect potential fraud. It implements a simple rule-based detection mechanism and is built with high availability and resilience in mind. The application is containerized using Docker and can be deployed on a Kubernetes cluster.

## Features
- Real-time fraud detection using a rule-based engine (Drools)
- High availability and resilience through Kubernetes deployment
- Low latency performance for transaction processing
- Distributed logging for better observability
- Comprehensive unit and integration tests

## Framework Selection
- **Spring Boot:**  
Provides a robust framework for building microservices with features like dependency injection, configuration management, and Actuator for monitoring.
- **Spring Actuator:**  
Provides production-ready features in Spring Boot applications, such as monitoring, metrics, health checks, and application management endpoints.
In this example, we set up an endpoint to shutdown the fraud detection service gracefully (triggered via preStop hook in kubernetes) by first stopping emssage reception from SQS, and then wait for all theads in the thread pool to terminate.
- **Spring security:**  
Provides protection to the sensitive shutdown endpoint using BASIC authentication. 
- **Drools:**  
A rule-based engine used to define and execute fraud detection rules dynamically. Rules are stored in rules.drl and can be updated without modifying the application code.
- **AWS SQS:**  
Used as a message queue to decouple components and enable asynchronous processing.
Two queues are used:
Input Queue: Receives transactions to be analyzed.
Output Queue: Stores flagged transactions for further downstream processing.
- **Kubernetes (EKS):**  
Ensures scalability and high availability by orchestrating containerized services. Docker image will be stored in the ECR service.   
The Fraud Detection Service requires access to CloudWatch and SQS, which is enabled by associating an IAM policy and role with the ServiceAccount using IRSA (IAM Roles for Service Accounts).
HPA (Horizontal Pod Autoscaler) is configured to automatically scale the Fraud Detection Service based on CPU usage.
- **AWS CloudWatch:**  
To enable amazon-cloudwatch-observability EKS addon for centralized logging and monitoring for the Fraud Detection Service, 

# Transaction Processing Flow
1. **Input Queue:**
Transactions are sent to the SQS Input Queue by external systems (e.g., payment gateways).  
In this example, trans-generator takes responsibility for creating random transaction data. 
2. **Fraud Detection Service:**  
The service pulls transactions from the Input Queue.  
Transactions are analyzed using Drools rules to detect potential fraud. The analysis will run in parallel via a thread pool.  
Flagged transactions are sent to the SQS Output Queue for further processing.
3. **Output Queue:**  
Downstream systems (e.g., manual review systems) consume flagged transactions from the Output Queue for further investigation.  

# Architecture Diagram
```
+-------------------+       +-------------------+       +-------------------+
|                   |       |                   |       |                   |
|  SQS Input Queue  | ----> | Fraud Detection   | ----> | SQS Output Queue  |
|                   |       | Service           |       |                   |
+-------------------+       |                   |       +-------------------+
                            |  - Spring Boot    |
                            |  - Drools Rules   |
                            |                   |
                            +-------------------+
                                    |
                                    v
                          +-------------------+
                          |                   |
                          | AWS CloudWatch    |
                          | (Logging/Monitor) |
                          |                   |
                          +-------------------+
```
# Scalability and Resilience
**Kubernetes (EKS):**  
Ensures horizontal scaling of the Fraud Detection Service based on transaction volume.
Uses Helm charts for deployment and configuration management.  
**AWS SQS:**  
Decouples components, ensuring resilience and fault tolerance.
# Project Structure
```
├── README.md   
├── docker
│   ├── aws                       Python Script init the aws cloud 
│   ├── fraud-detection-service   Dockerfile of fraud-detection-service 
│   ├── trans-generator           Dockerfile of trans-generator
│   ├── helm                      K8S helm package script deploying the artifact to EKS
│   ├── package.sh                package the jar and build the app docker image
├── fraud-detection-service
│   ├── pom.xml
│   ├── src
│       ├── main
│       │   ├── java
│       │   │   └── com
│       │   └── resources
│       │       ├── application.yml
│       │       ├── logback-spring.xml
│       │       └── rules.drl
│       └── test
│           ├── java
│           │   └── com
│           └── resources
│               └── application-test.yml
└── trans-generator
│   ├── pom.xml
│   ├── src
│   │   ├── main
│   │   │   ├── java
│   │   │   │   └── com
│   │   │   └── resources
│   │   │       ├── application.yml
│   │   │       └── logback-spring.xml
│   │   └── test
│   │       ├── java
│   │       └── resources
├── pom.xml

```

## Getting Started
1. **Clone the repository:**
   ```
   git clone <repository-url>
   ```
2. **Init AWS Cloud Infracture:**
   - **Create your own ACCESS KEY and SECRET KEY**  
   For local test, you should create you own AWS IAM ACCESS KEY and SECRET KEY. 
   For security, you can set your key to the environment variables, 
   ```
   export AWS_ACCESS_KEY=your_own_access_key
   export AWS_SECRET_KEY=your_own_secret_key
   ```
   These variables will be read by the spring boot in the bootstrap, avoiding hardcode in the application.yaml. 
   - **Create EKS cluster:**
   Follow https://docs.aws.amazon.com/eks/latest/userguide/create-cluster.html to create your own EKS cluster. 


   - **Run the aws/init.py script.**   
   ```
      % cd docker/aws
      % python -m venv .venv
      % pip install -r requirements.txt
      % python init.py

      Repository 'example/fraud-detection-service' created successfully.
      Repository URI: xxxxxxxxxxxx.dkr.ecr.ap-east-1.amazonaws.com/example/fraud-detection-service
      Repository 'example/trans-generator' created successfully.
      Repository URI: xxxxxxxxxxxx.dkr.ecr.ap-east-1.amazonaws.com/example/trans-generator
      Queue 'notify-queue' created successfully.
      Queue URL: https://sqs.ap-east-1.amazonaws.com/xxxxxxxxxxxx/notify-queue
      Queue 'transaction-queue' created successfully.
      Queue URL: https://sqs.ap-east-1.amazonaws.com/xxxxxxxxxxxx/transaction-queue
      Policy 'FraudDetectionPolicy' created successfully.
      Policy ARN: arn:aws:iam::xxxxxxxxxxxx:policy/FraudDetectionPolicy
   ```
   
   a. create the ECR repository to store the docker image.  
      Please Markdown the URI Domain。   
   b. create the Standard Queues (notify-queue, transaction-queue)  
   c. create the IAM Policy for accessing SQS and CloudWatch service
      Please markdown the policy Arn. 
   ```
   {
      "Version": "2012-10-17",
      "Statement": [
         {
            "Effect": "Allow",
            "Action": [
               "sqs:SendMessage",
               "sqs:ReceiveMessage",
               "sqs:DeleteMessage",
               "sqs:GetQueueAttributes"
            ],
            "Resource": "*"
         }
      ]
   }
   ```
   For production usage the Resource should be more specific.

   - **Create IAM Service Account for the App**
   ```
   eksctl create iamserviceaccount \
      --name fraud-detection-service-account \
      --namespace default \
      --cluster devops \
      --attach-policy-arn arn:aws:iam::xxxxxxxxxxxx:policy/FraudDetectionPolicy \
      --approve
   ```
   You get the service account using in the eks cluster deployment. 
   
   Avoid the leak of AK/SK, IRSA should be used in the production environment.

3. **Build the application:**
   Before you run the following script, the REPO_DOMAIN variable should be replaced with the value in step 
   ```
   cd docker
   chmod +x package.sh
   ./package.sh
   ```
   The package.sh will compile, package, build the docker image, and push to the ECR repository.

4. **Deploy to Kubernetes:**
   - Installing Helm  (https://helm.sh/docs/intro/install/)
   - Configure the var in the values.yaml.  
   
   ```
   cd docker
   helm install frauddetection ./helm
   ```
5. **Enable amazon-cloudwatch-observability addon for EKS**  
   
   ```
   aws iam attach-role-policy \
   --role-name my-role \
   --policy-arn arn:aws:iam::aws:policy/CloudWatchAgentServerPolicy

   aws eks create-addon \
   --addon-name amazon-cloudwatch-observability \
   --cluster-name my-cluster-name \
   --pod-identity-associations serviceAccount=cloudwatch-agent,roleArn=arn:aws:iam::111122223333:role/my-role
   ```

   Reference : https://docs.aws.amazon.com/AmazonCloudWatch/latest/monitoring/install-CloudWatch-Observability-EKS-addon.html

## Testing

- Unit tests can be run using:
```
mvn test
```

- Integration tests can be executed with:
```
mvn clean verify
```
Check the JaCoCo report in the fraud-detection-service/target/site/jacoco/index.htm

For convenience, I have placed a test report in the test-report directory.

## Logging

The application uses Logback for logging. Configuration can be found in `src/main/resources/logback-spring.xml`.
