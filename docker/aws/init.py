import boto3
import json

def create_ecr_repository(repository_name):
    """
    create ecr repository 
    :param repository_name str: The name of the ECR repository to create.
    """
    ecr_client = boto3.client('ecr')

    try:
        response = ecr_client.create_repository(
            repositoryName=repository_name
        )
        print(f"Repository '{repository_name}' created successfully.")
        print("Repository URI:", response['repository']['repositoryUri'])
    except ecr_client.exceptions.RepositoryAlreadyExistsException:
        print(f"Repository '{repository_name}' already exists.")
    except Exception as e:
        print(f"Error creating repository '{repository_name}': {e}")
def create_sqs_queue(queue_name):
    """
    Create an SQS standard queue.
    :param queue_name str: The name of the SQS queue to create.
    """
    sqs_client = boto3.client('sqs')

    try:
        response = sqs_client.create_queue(
            QueueName=queue_name,
            Attributes={
                'DelaySeconds': '0',
                'MessageRetentionPeriod': '345600'  # 4 days
            }
        )
        print(f"Queue '{queue_name}' created successfully.")
        print("Queue URL:", response['QueueUrl'])
    except Exception as e:
        print(f"Error creating queue '{queue_name}': {e}")

def create_iam_policy(policy_name, policy_document):
    """
    Create an IAM policy.
    :param policy_name str: The name of the IAM policy to create.
    :param policy_document dict: The policy document in JSON format.
    """
    iam_client = boto3.client('iam')

    try:
        response = iam_client.create_policy(
            PolicyName=policy_name,
            PolicyDocument=json.dumps(policy_document)
        )
        print(f"Policy '{policy_name}' created successfully.")
        print("Policy ARN:", response['Policy']['Arn'])
    except iam_client.exceptions.EntityAlreadyExistsException:
        print(f"Policy '{policy_name}' already exists.")
    except Exception as e:
        print(f"Error creating policy '{policy_name}': {e}")

if __name__ == "__main__":
    # create_ecr_repository("example/fraud-detection-service")
    # create_ecr_repository("example/trans-generator")

    # create_sqs_queue("notify-queue")
    # create_sqs_queue("transaction-queue")

    policy_document = {
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

    create_iam_policy("FraudDetectionPolicy", policy_document)