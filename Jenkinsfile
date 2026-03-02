pipeline {
    agent any

    // Inyectamos la credencial aquí
    environment {
        WEBHOOK_URL = credentials('SLACK_WEBHOOK_URL')
    }

    parameters {
        // Esto crea el menú en la interfaz de Jenkins
        choice(name: 'BROWSER', choices: ['chrome', 'firefox'], description: '¿En qué navegador quieres ejecutar?')
        string(name: 'ENV', defaultValue: 'https://www.saucedemo.com/', description: 'URL del ambiente')
    }

    tools {
        // Usa los nombres que configuraste en "Global Tool Configuration"
        maven 'MAVEN_HOME'
        jdk 'JAVA_HOME'
    }

    stages {
        stage('Checkout') {
            steps {
                // Jenkins descargará el código solo
                echo 'Descargando código de GitHub...'
                checkout scm
            }
        }

        stage('Build & Compile') {
            steps {
                sh 'mvn clean compile'
            }
        }

        stage('UI Tests (Selenium)') {
            steps {
                script {
                    sh '''
                        # 1. Limpieza de entorno para evitar conflictos de librerías
                        unset LD_LIBRARY_PATH
                        export MOZ_HEADLESS=1
                        
                        # 2. Verificación de binarios
                        google-chrome --version || echo "Chrome falló"
                        firefox --version || echo "Firefox falló"
                        
                        # 3. Ejecución de la suite
                        mvn test -DsuiteXmlFile=testng.xml
                    '''
                }
            }
        }
    }

    post {
        always {
            allure includeProperties: false, jdk: '', results: [[path: 'allure-results']]
            archiveArtifacts artifacts: 'target/screenshots/*.png', allowEmptyArchive: true
        }
        success {
            sh """
                curl -H "Content-Type: application/json" -X POST \
                -d '{"content": "✅ **¡Build Exitoso!**\\nProyecto: ${env.JOB_NAME}\\nReporte: ${env.BUILD_URL}allure"}' \
                \$WEBHOOK_URL
            """
        }
        failure {
            sh """
                curl -H "Content-Type: application/json" -X POST \
                -d '{"content": "❌ **¡Alerta de Fallo!**\\nProyecto: ${env.JOB_NAME}\\nEvidencia: ${env.BUILD_URL}allure"}' \
                \$WEBHOOK_URL
            """
        }
    }
}