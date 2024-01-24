import SwiftUI

@main
struct PasswordEncryptorApp: App {
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}

struct ContentView: View {
    @State private var password: String = ""
    @State private var action: Action = .none

    enum Action {
        case encrypt
        case decrypt
        case none
    }

    var body: some View {
        VStack {
            Text("Password Encryptor")
                .font(.title)
                .padding()

            SecureField("Enter password", text: $password)
                .textFieldStyle(RoundedBorderTextFieldStyle())
                .padding()

            HStack {
                Button("Encrypt") {
                    action = .encrypt
                }
                .padding()

                Button("Decrypt") {
                    action = .decrypt
                }
                .padding()
            }

            Spacer()
        }
        .padding()
        .onChange(of: action) { newAction in
            if newAction == .encrypt {
                do {
                    let encryptor = Encryptor()
                    let sourceFilePath = "passwords.txt"
                    let encryptedFilePath = "encrypted_passwords.txt"

                    try encryptor.encryptFile(atPath: sourceFilePath, toPath: encryptedFilePath, usingPassword: password)
                    print("File encrypted successfully.")
                } catch {
                    print("Error encrypting the file: \(error.localizedDescription)")
                }
            } else if newAction == .decrypt {
                do {
                    let encryptor = Encryptor()
                    let encryptedFilePath = "encrypted_passwords.txt"

                    try encryptor.decryptFile(atPath: encryptedFilePath, toPath: "decrypted_passwords.txt", usingPassword: password)
                    print("File decrypted successfully.")
                } catch {
                    print("Error decrypting the file: \(error.localizedDescription)")
                }
            }

            action = .none
        }
    }
}

@main
struct PasswordEncryptorApp: App {
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
