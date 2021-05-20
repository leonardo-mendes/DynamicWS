{{/*
Expand the name of the chart.
*/}}
{{- define "microservice.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Expand the name of the chart.
*/}}
{{- define "microservice.fullname" -}}
{{- printf "%s" .Chart.Name | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "microservice.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Common labels
*/}}
{{- define "microservice.labels" -}}
helm.sh/chart: {{ include "microservice.chart" . }}
{{ include "microservice.selectorLabels" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end }}

{{/*
Selector labels
*/}}
{{- define "microservice.selectorLabels" -}}
app.kubernetes.io/name: {{ include "microservice.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
app: {{ include "microservice.fullname" . }}
environment: {{ .Values.environment | quote }}
module: {{ .Values.module | quote }}
{{- end }}

{{/*
Create the name of the service account to use
*/}}
{{- define "microservice.serviceAccountName" -}}
{{- if .Values.serviceAccount.create }}
{{- default (include "microservice.fullname" .) .Values.serviceAccount.name }}
{{- else }}
{{- default "default" .Values.serviceAccount.name }}
{{- end }}
{{- end }}

{{/*
Enviroment configuration
*/}}
{{- define "helpers.list-env-variables"}}
{{- range $key, $val := .Values.container.env.secret }}
- name: {{ $key }}
  valueFrom:
    secretKeyRef:
      name: {{ (split ":" $val)._1 | quote }}
      key: {{ (split ":" $val)._0 }}
{{- end}}
{{- range $key, $val := .Values.container.env.fieldRef }}
- name: {{ $key }}
  valueFrom:
    fieldRef:
      apiVersion: {{ (split ":" $val)._0 | quote }}
      fieldPath: {{ (split ":" $val)._1 }}
{{- end}}
{{- range $key, $val := .Values.container.env.configMap }}
- name: {{ $key }}
  valueFrom:
    configMapKeyRef:
      name: {{ (split ":" $val)._0 | quote }}
      key: {{ (split ":" $val)._1 }}
{{- end}}
{{- range $key, $val := .Values.container.env.resource }}
- name: {{ $key }}
  valueFrom:
    resourceFieldRef:
      containerName: {{ (split ":" $val)._0 | quote }}
      resource: {{ (split ":" $val)._1 }}
{{- end}}
{{- range $key, $val := .Values.container.env.default }}
- name: {{ $key }}
  value: {{ $val | quote }}
{{- end}}
{{- end }}