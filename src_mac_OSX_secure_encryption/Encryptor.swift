import Foundation
import CommonCrypto

class Encryptor {
    func encryptFile(atPath sourcePath: String, toPath destinationPath: String, usingPassword password: String) throws {
        let data = try Data(contentsOf: URL(fileURLWithPath: sourcePath))
        let key = passwordToData(password: password)

        let encryptedData = try encrypt(data: data, key: key)

        try encryptedData.write(to: URL(fileURLWithPath: destinationPath))
    }

    func decryptFile(atPath sourcePath: String, toPath destinationPath: String, usingPassword password: String) throws {
        let encryptedData = try Data(contentsOf: URL(fileURLWithPath: sourcePath))
        let key = passwordToData(password: password)

        let decryptedData = try decrypt(data: encryptedData, key: key)

        try decryptedData.write(to: URL(fileURLWithPath: destinationPath))
    }

    private func passwordToData(password: String) -> Data {
        return Data(password.utf8)
    }

    private func encrypt(data: Data, key: Data) throws -> Data {
        var buffer = [UInt8](repeating: 0, count: data.count + kCCBlockSizeAES128)
        var numBytesEncrypted = 0

        let cryptStatus = key.withUnsafeBytes { keyBytes in
            data.withUnsafeBytes { dataBytes in
                CCCrypt(
                    UInt32(kCCEncrypt),
                    UInt32(kCCAlgorithmAES128),
                    UInt32(kCCOptionPKCS7Padding),
                    keyBytes.baseAddress, kCCKeySizeAES128,
                    nil,
                    dataBytes.baseAddress, data.count,
                    &buffer, buffer.count,
                    &numBytesEncrypted
                )
            }
        }

        if cryptStatus == CCCryptorStatus(kCCSuccess) {
            return Data(buffer.prefix(numBytesEncrypted))
        } else {
            throw EncryptionError.encryptionFailed
        }
    }

    private func decrypt(data: Data, key: Data) throws -> Data {
        var buffer = [UInt8](repeating: 0, count: data.count + kCCBlockSizeAES128)
        var numBytesDecrypted = 0

        let cryptStatus = key.withUnsafeBytes { keyBytes in
            data.withUnsafeBytes { dataBytes in
                CCCrypt(
                    UInt32(kCCDecrypt),
                    UInt32(kCCAlgorithmAES128),
                    UInt32(kCCOptionPKCS7Padding),
                    keyBytes.baseAddress, kCCKeySizeAES128,
                    nil,
                    dataBytes.baseAddress, data.count,
                    &buffer, buffer.count,
                    &numBytesDecrypted
                )
            }
        }

        if cryptStatus == CCCryptorStatus(kCCSuccess) {
            return Data(buffer.prefix(numBytesDecrypted))
        } else {
            throw EncryptionError.decryptionFailed
        }
    }

    enum EncryptionError: Error {
        case encryptionFailed
        case decryptionFailed
    }
}
