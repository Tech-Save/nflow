create index if not exists nflow_workflow_polling on nflow_workflow(next_activation, status, executor_id, executor_group);