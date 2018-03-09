#!/bin/bash

# created S3 mock-domains
cat <<EOT >> /etc/hosts
127.0.0.1	mockbucket1.localhost
127.0.0.1	mockbucket2.localhost
127.0.0.1	mockbucket3.localhost
EOT
