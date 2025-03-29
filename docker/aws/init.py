import boto3

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

if __name__ == "__main__":
    # 创建两个 ECR 存储库
    create_ecr_repository("example/fraud-detection-service")
    create_ecr_repository("example/trans-generator")