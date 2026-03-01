pipeline {
    agent any
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
                        # Verificamos si los navegadores responden
                        google-chrome --version || echo "Chrome no encontrado"
                        firefox --version || echo "Firefox no encontrado"

                        export MOZ_HEADLESS=1
                        mvn test -DsuiteXmlFile=testng.xml
                    '''
                }
            }
        }
    }

    post {
        always {
            echo 'Archivando resultados de las pruebas...'
            junit 'target/surefire-reports/*.xml'
            archiveArtifacts artifacts: 'target/surefire-reports/**', allowEmptyArchive: true
            archiveArtifacts artifacts: 'target/screenshots/*.png', allowEmptyArchive: true
            allure includeProperties: false, jdk: '', results: [[path: 'Allure_Report']]
        }
        success{
            echo "✅ ¡Victoria! El sistema está estable."
            // Aquí podrías usar el plugin de Slack:
            // slackSend color: 'good', message: "Build ${env.BUILD_NUMBER} exitoso en ${env.JOB_NAME}"
        }
        failure{
            archiveArtifacts artifacts: 'target/screenshots/*.png', allowEmptyArchive: true
            echo "❌ Alerta: El build ha fallado. Revisa la evidencia."
            echo "Revisa la evidencia aquí: ${env.BUILD_URL}allure"
        }
    }
}