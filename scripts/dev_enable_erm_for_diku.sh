#!/bin/bash

curl -XPOST http://localhost:9130/_/proxy/tenants/diku/modules -d '{"id": "olf-erm-1.0.0"}'
