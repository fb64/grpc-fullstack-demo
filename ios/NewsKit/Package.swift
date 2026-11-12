// swift-tools-version: 6.1
import PackageDescription

// gRPC client logic of the iOS app: generated code, repository and view model.
// It has no UIKit/SwiftUI dependency, so it also builds and tests on macOS and Linux (`swift test`).
let package = Package(
    name: "NewsKit",
    platforms: [.iOS(.v18), .macOS(.v15)],
    products: [
        .library(name: "NewsKit", targets: ["NewsKit"]),
    ],
    dependencies: [
        .package(url: "https://github.com/grpc/grpc-swift-2.git", from: "2.4.4"),
        .package(url: "https://github.com/grpc/grpc-swift-nio-transport.git", from: "2.12.0"),
        .package(url: "https://github.com/grpc/grpc-swift-protobuf.git", from: "2.5.0"),
        .package(url: "https://github.com/apple/swift-protobuf.git", from: "1.39.0"),
    ],
    targets: [
        .target(
            name: "NewsKit",
            dependencies: [
                .product(name: "GRPCCore", package: "grpc-swift-2"),
                .product(name: "GRPCNIOTransportHTTP2", package: "grpc-swift-nio-transport"),
                .product(name: "GRPCProtobuf", package: "grpc-swift-protobuf"),
                .product(name: "SwiftProtobuf", package: "swift-protobuf"),
            ]
        ),
        .testTarget(name: "NewsKitTests", dependencies: ["NewsKit"]),
    ]
)
