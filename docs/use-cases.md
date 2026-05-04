# Common Use Cases

Practical patterns for everyday loading states.

## Loading a List of Items

A common pattern is to show placeholder rows while a list loads. Wrap the list in a [`PlaceholderSurface`](coordinated.md) so every row shimmers in lockstep.

```kotlin
@Composable
fun UserList(users: List<User>?, isLoading: Boolean) {
    PlaceholderSurface {
        LazyColumn {
            items(if (isLoading) 5 else users?.size ?: 0) { index ->
                UserListItem(
                    user = users?.getOrNull(index),
                    isLoading = isLoading,
                )
            }
        }
    }
}

@Composable
fun UserListItem(user: User?, isLoading: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .size(48.dp)
                .placeholder(
                    enabled = isLoading,
                    shape = CircleShape,
                    highlight = PlaceholderDefaults.shimmer,
                ),
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            // Name
            Text(
                text = user?.name ?: "Loading name",
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .placeholder(
                        enabled = isLoading,
                        shape = RoundedCornerShape(4.dp),
                    ),
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Email
            Text(
                text = user?.email ?: "Loading email",
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .placeholder(
                        enabled = isLoading,
                        shape = RoundedCornerShape(4.dp),
                    ),
            )
        }
    }
}
```

## Loading an Image

Perfect for showing a skeleton while images load from the network.

```kotlin
var isLoading by remember { mutableStateOf(true) }

AsyncImage(
    model = imageUrl,
    contentDescription = "Product image",
    onSuccess = { isLoading = false },
    modifier = Modifier
        .fillMaxWidth()
        .height(200.dp)
        .placeholder(
            enabled = isLoading,
            highlight = PlaceholderDefaults.shimmer,
        ),
)
```

## Card with Multiple Placeholders

```kotlin
@Composable
fun ProductCard(product: Product?, isLoading: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Product image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .placeholder(
                        enabled = isLoading,
                        shape = RoundedCornerShape(8.dp),
                        highlight = PlaceholderDefaults.shimmer,
                    ),
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Product title
            Text(
                text = product?.title ?: "Loading title",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .placeholder(
                        enabled = isLoading,
                        shape = RoundedCornerShape(4.dp),
                    ),
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Product description
            Text(
                text = product?.description ?: "",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .placeholderText(
                        enabled = isLoading,
                        lines = 3,
                        lastLineFraction = 0.55f,
                        style = MaterialTheme.typography.bodyMedium,
                        shape = RoundedCornerShape(4.dp),
                    ),
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Price
            Text(
                text = product?.price ?: "$0.00",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier
                    .width(80.dp)
                    .placeholder(
                        enabled = isLoading,
                        shape = RoundedCornerShape(4.dp),
                        highlight = PlaceholderDefaults.pulse,
                    ),
            )
        }
    }
}
```

This card combines `Modifier.placeholder` (single-rect shapes for image, title, price) with `Modifier.placeholderText` (multi-line bars for description) — each scaling its own appearance from the same `enabled` flag.
